/*
 * Copyright (C) 2018 E.S.R.Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mindroid.runtime.system.aio;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import mindroid.os.Bundle;
import mindroid.util.Log;

public abstract class AbstractClient {
    private final String LOG_TAG;
    private static final boolean DEBUG = false;

    private final SocketExecutorGroup mExecutorGroup = new SocketExecutorGroup();
    private final Socket mSocket;
    private final String mHost;
    private final int mPort;
    private final Connection mConnection;
    private final int mNodeId;

    public AbstractClient(int nodeId, String uri) throws IOException {
        LOG_TAG = "Client [" + uri + "]";
        mNodeId = nodeId;

        try {
            URI url = new URI(uri);
            if (!"tcp".equals(url.getScheme())) {
                throw new IllegalArgumentException("Invalid URI scheme: " + url.getScheme());
            }
            mHost = url.getHost();
            mPort = url.getPort();
            mSocket = new Socket();

            mSocket.connect(new InetSocketAddress(mHost, mPort)).whenComplete((value, exception) -> {
               if (exception != null) {
                   Log.e(LOG_TAG, exception.getMessage(), exception);
                   shutdown();
               }
            });
            mExecutorGroup.register(mSocket);
            mConnection = new Connection(mSocket);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI: " + uri);
        }
    }

    protected void shutdown() {
        if (mConnection != null) {
            try {
                mConnection.close();
            } catch (IOException ignore) {
            }
        }

        mExecutorGroup.shutdown();
    }

    public int getNodeId() {
        return mNodeId;
    }

    public abstract void onTransact(Bundle context, InputStream inputStream, OutputStream outputStream) throws IOException;

    public Bundle getContext() {
        return mConnection.mContext;
    }

    public InputStream getInputStream() {
        return mConnection.mSocket.getInputStream();
    }

    public OutputStream getOutputStream() {
        return mConnection.mSocket.getOutputStream();
    }

    public class Connection implements Closeable {
        private final Bundle mContext = new Bundle();
        private final Socket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public Connection(Socket socket) {
            mContext.putObject("connection", this);
            mSocket = socket;
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mSocket.getInputStream().setListener((operation, argument) -> {
                if (operation == Socket.OP_READ) {
                    try {
                        AbstractClient.this.onTransact(mContext, mInputStream, mOutputStream);
                    } catch (IOException e) {
                        if (DEBUG) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                        try {
                            close();
                        } catch (IOException ignore) {
                        }
                        shutdown();
                    }
                } else if (operation == Socket.OP_CLOSE) {
                    if (DEBUG) {
                        if (argument != null) {
                            Exception e = (Exception) argument;
                            Log.e(LOG_TAG, "Socket has been closed: " + e.getMessage(), e);
                        } else {
                            Log.e(LOG_TAG, "Socket has been closed");
                        }
                    }
                    try {
                        close();
                    } catch (IOException ignore) {
                    }
                    shutdown();
                }
            });
        }

        @Override
        public void close() throws IOException {
            if (DEBUG) {
                Log.d(LOG_TAG, "Closing connection");
            }
            mExecutorGroup.unregister(mSocket);
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Cannot close socket", e);
            }
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException ignore) {
                }
            }
            if (mOutputStream != null) {
                try {
                    mOutputStream.close();
                } catch (IOException ignore) {
                }
            }
            if (DEBUG) {
                Log.d(LOG_TAG, "Connection has been closed");
            }
        }
    }
}

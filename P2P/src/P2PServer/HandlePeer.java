package P2PServer;

import java.io.IOException;

public class HandlePeer implements Runnable
{
    Peer peer;
    HandlePeer(Peer peer)
    {
        this.peer = peer;
    }

    @Override
    public void run()
    {
        boolean endSession = false;
        while(true)
        {
            try {
                if(endSession) {
                    synchronized (this) {
                        Server.numberOfConnections--;
                        try {
                            peer.dos.close();
                            peer.dis.close();
                            peer.oos.close();
                            peer.ois.close();
                            peer.peerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                String flag = peer.dis.readUTF();
                switch (flag)
                {
                    case "#DISCONNECT":
                    {
                        endSession = true;
                        break;
                    }
                    case "#SENDCOMMENT":
                    {

                        break;
                    }
                    case "#GETCOMMENT":
                    {

                        break;
                    }
                    case "#GETVIDEO":
                    {
                        peer.dos.writeUTF(Server.getFtpUsername());
                        peer.dos.writeUTF(Server.getFtpPasswd());
                        break;
                    }
                    default:
                    {
                        System.out.println("Ye kya flag hai be "+flag);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                try {
                    peer.dos.close();
                    peer.dis.close();
                    peer.oos.close();
                    peer.ois.close();
                    peer.peerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

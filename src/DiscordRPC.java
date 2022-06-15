/*
import net.arikia.dev.drpc.*;
import net.arikia.dev.drpc.callbacks.ReadyCallback;


public class DiscordRPC {

    public boolean running = true;
    private long created =0;

    public void start(){
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handles = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser user){
                System.out.println("Websome"+ user.username + "#"+user.discriminator + ".");
                update("Booting","");
            }
        }).build();
        DiscordRPC.discordInitialize("",handles,true);
        new Thread("RPC"){
            @Override
            public void run(){
                while (running){
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();


    }



    public void shutdown(){


    }
    public void update(String firstLine, String secondline){


    }

}
*/

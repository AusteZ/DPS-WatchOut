package administration_client.userinterface;

public abstract class UserInterface {
    protected final UserInterfaceBridge userInterfaceBridge;

    protected UserInterface(UserInterfaceBridge userInterfaceBridge){
        this.userInterfaceBridge = userInterfaceBridge;
    }

    public abstract void runInterface();
}

import java.util.ArrayList;

public class DataClients implements Registration{

    ArrayList <Client> clients;

    public void add (Client client){
        clients.add(client);
    }

    public Client getClient (String nickName){
        for (int i = 0; i <clients.size() ; i++) {
            if (clients.get(i).getNickName().equals(nickName)){
                return clients.get(i);
            }
        }
        return null;
    }

    public boolean checkPassword (Client client, String password){
        if (client.getPassword().equals(password)){
            return true;
        }
        return false;
    }

    public boolean isClientExist (String nickName){
        Client client = null;
        if (getClient(nickName) != null){
            return true;
        }
        return false;
    }

    public void showAllUsers (){
        for (int i = 0; i <clients.size() ; i++) {
            System.out.println(clients.get(i).toString());
        }
    }

    public ArrayList allUsers (){
        return clients;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <clients.size() ; i++) {
            sb.append(clients.get(i).toString()).append("; ");
        }
        return sb.toString();
    }
}

public interface Registration {

    void add(Client client);

    Client getClient(String nickName);

    boolean checkPassword(Client client, String password);

    boolean isClientExist(String nickName);
}

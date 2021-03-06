import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class ClientStorageController {

    // что доделать:
    // 4. Разобраться с закрытием окна регистрации
    // 5. Сделать нормальное закрытие потоков, клиентов

    private static final Logger LOG = LoggerFactory.getLogger(ClientStorageController.class);

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;

    public TextArea ta_clientName;
    public TextArea ta_serverName;
    public TextArea ta_selectedFileName;

    public Stage stage;

    public Path clientFolder;
    public Path serverFolder;
    public AnchorPane mainStorage;

    private boolean authentif;
     RegistrationController regController;
    private Stage regStage;


    public void start() {
            startProcess();
    }


    private void startProcess() {
        clientFolder = Paths.get("FolderClient");
        serverFolder = Paths.get("FolderServer");
        ta_serverName.appendText("Server Storage");
        ta_serverName.setEditable(false);
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Message message = GoToNet.readObject();

                    if (message instanceof InfoMessage){
                        InfoMessage info = (InfoMessage) message;
                        regController.ta_result.clear();
                        regController.ta_result.appendText(info.getMessage());
                    }

                    if (message instanceof FilePath) {
                        FilePath fp = (FilePath) message;
                        if (!Files.exists(clientFolder.resolve(ta_clientName.getText()).resolve(fp.getFileName()))) {
                            Files.createFile(clientFolder.resolve(ta_clientName.getText()).resolve(fp.getFileName()));
                        }
                            Files.write(clientFolder.resolve(ta_clientName.getText()).resolve(fp.getFileName()), fp.getPathData(), StandardOpenOption.APPEND);
                    }


                    if (message instanceof RequestRename) {
                        RequestRename fileName = (RequestRename) message;
                        if (Files.exists(clientFolder.resolve(ta_clientName.getText()).resolve(fileName.getOldName()))) {
                            Path file = clientFolder.resolve(ta_clientName.getText()).resolve(fileName.getOldName());
                            Files.move(file, file.resolveSibling(fileName.getNewName()));
                            refreshLists(ta_clientName.getText());
                        }
                    }
                    if (message instanceof RequestDelete) {
                        RequestDelete fileName = (RequestDelete) message;
                        if (fileName.getPlaceWhereDelete() == 'c') {
                            Files.deleteIfExists(clientFolder.resolve(ta_clientName.getText()).resolve(fileName.getFileName()));
                            refreshLists(ta_clientName.getText());
                        }
                    }
                    if (message instanceof AuthForm){
                        AuthForm auth = (AuthForm) message;
                        regController.registrationForm.setVisible(false);
                        regController.showCloudStorage(auth.getNickName());
                        ta_clientName.appendText(auth.getNickName());
                        if (!Files.exists(clientFolder.resolve(auth.getNickName()))) {
                            Files.createDirectory(clientFolder.resolve(auth.getNickName()));
                        }
                        refreshLists(ta_clientName.getText());
                    }
                    if (message instanceof RequestAllUsers){
                        RequestAllUsers users = (RequestAllUsers) message;
                        regController.ta_result.clear();
                        regController.ta_result.appendText(users.getUsers());
                    }
                    refreshLists(ta_clientName.getText());
                }
            } catch (Exception e) {
                LOG.error("e = ", e);
            } finally {
                try {
                    GoToNet.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();

    }

    private void regWindow() {
        this.mainStorage.setVisible(false);
        regController.getRegistrationForm().setVisible(true);
    }

    public void setAuthentif(boolean authentif) {
        this.authentif = authentif;
        mainStorage.setVisible(authentif);
        regController.getRegistrationForm().setVisible(!authentif);
    }

    public void download(ActionEvent actionEvent) {
        GoToNet.sendMessToServer(new RequestDownload(String.valueOf(listViewServer.getSelectionModel().getSelectedItem())));
    }


    public void upload(ActionEvent event) throws IOException {
        Path path = clientFolder.resolve(ta_clientName.getText()).resolve(listViewClient.getSelectionModel().getSelectedItem());
        FilePackage upload = new FilePackage(path);
        DataInputStream in = new DataInputStream(new FileInputStream(String.valueOf(clientFolder.resolve(ta_clientName.getText()).resolve(upload.getFileName()))));
        FilePath fp;
        int read;
        int numberPath = 0;
        byte[] buffer = new byte[1024];
        while ((read = in.read(buffer)) != -1) {
            if (read < buffer.length) {
                byte[] buff = new byte[read];
                System.arraycopy(buffer, 0, buff, 0, read);
                numberPath++;
                fp = new FilePath(numberPath, true, upload.getFileName(), null, read);
                GoToNet.sendMessToServer(fp);
            } else {
                numberPath++;
                fp = new FilePath(numberPath, false, upload.getFileName(), buffer, read);
                GoToNet.sendMessToServer(fp);
            }
        }
        in.close();
        listViewServer.getItems().add(upload.getFileName());
    }


    public void rename(ActionEvent actionEvent) {
        String fileNameToRename = null;
        if (listViewClient.getSelectionModel().getSelectedItem() != null) {
            fileNameToRename = listViewClient.getSelectionModel().getSelectedItem();
        } else if (listViewServer.getSelectionModel().getSelectedItem() != null) {
            fileNameToRename = listViewServer.getSelectionModel().getSelectedItem();
        } else {
            LOG.debug("no selected file to rename");
        }
        GoToNet.sendMessToServer(new RequestRename(fileNameToRename, ta_selectedFileName.getText()));
        refreshLists(ta_clientName.getText());
    }

    public void delete(ActionEvent actionEvent) {
        String fileNameToDelete = null;
        char placeWhereDelete = 0;
        if (listViewClient.getSelectionModel().getSelectedItem() != null) {
            fileNameToDelete = listViewClient.getSelectionModel().getSelectedItem();
            placeWhereDelete = 'c';
        } else if (listViewServer.getSelectionModel().getSelectedItem() != null) {
            fileNameToDelete = listViewServer.getSelectionModel().getSelectedItem();
            placeWhereDelete = 's';
        } else {
            LOG.debug("no selected file to delete");
        }
        GoToNet.sendMessToServer(new RequestDelete(fileNameToDelete, placeWhereDelete));
        refreshLists(ta_clientName.getText());
    }

    public List<String> refresh(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            Files.createDirectory(folder);
        }
        File folderClient = folder.toFile();
        File[] files = folderClient.listFiles();
        List fileList = new ArrayList();
        if (files.length == 0) {
            fileList.add(" ... empty ... ");
        } else {
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i].getName());
            }
        }
        return fileList;
    }

    public void refreshLists(String nickName) {
        Platform.runLater(() -> {
            listViewServer.getItems().clear();
            try {
                listViewServer.getItems().addAll(refresh(serverFolder.resolve(nickName)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Platform.runLater(() -> {
            listViewClient.getItems().clear();
            try {
                listViewClient.getItems().addAll(refresh(clientFolder.resolve(nickName)));
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

    public void tryRegistr(String nick, String password) {
        GoToNet.sendMessToServer(new RegistrationForm(nick, password));
    }

    public void tryAuth(String nick, String password) {
        GoToNet.sendMessToServer(new AuthForm(nick, password));
    }
    public void askForUsers (){
        GoToNet.sendMessToServer(new RequestAllUsers ());
    }

    public void setRegController(RegistrationController regController) {
        this.regController = regController;
    }
}

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import model.*;
import org.omg.CORBA.TIMEOUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ClientStorageController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientStorageController.class);

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;

    public TextArea ta_clientName;
    public TextArea ta_serverName;
    public TextArea ta_selectedFileName;

    public Path clientFolder;
    public Path serverFolder;

        @Override
    public void initialize(URL location, ResourceBundle resources) {
            GoToNet.start();
            clientFolder = Paths.get("FolderClient");
            serverFolder = Paths.get("FolderServer");
            ta_serverName.appendText("Server Storage");
            ta_serverName.setEditable(false);
            Thread t = new Thread(() -> {
                    try {
                        while (true) {
                            Message message = GoToNet.readObject();
                            if (message instanceof FilePackage) {
                                FilePackage filePackage = (FilePackage) message;
                                Files.write(clientFolder.resolve(ta_clientName.getText()).resolve(filePackage.getFileName()), filePackage.getFileBytes(), StandardOpenOption.CREATE);
                                refreshLists(ta_clientName.getText());
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
                                if (fileName.getPlaceWhereDelete() == 'c'){
                                    Files.deleteIfExists(clientFolder.resolve(ta_clientName.getText()).resolve(fileName.getFileName()));
                                    refreshLists(ta_clientName.getText());
                                }
                            }
                            if (message instanceof ClientDelails){
                                ClientDelails nickName = (ClientDelails) message;
                                ta_clientName.appendText(nickName.getNickName());
                                ta_clientName.setEditable(false);
//                                clientFolder = Paths.get("FolderClient", nickName.getNickName());
//                                serverFolder = Paths.get("FolderClient", nickName.getNickName());
                                if (!Files.exists(clientFolder)){
                                    Files.createDirectory(clientFolder);
                                }
                                refreshLists(ta_clientName.getText());
                            }
                        }
                    }catch (Exception e) {
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

//            listViewClient.getItems().addAll(String.valueOf(clientFolder.resolve(ta_clientName.getText())));
//            listViewServer.getItems().addAll(String.valueOf(serverFolder.resolve(ta_clientName.getText())));
        }

    public void download(ActionEvent actionEvent) {
        GoToNet.sendMessToServer(new RequestDownload(String.valueOf(listViewServer.getSelectionModel().getSelectedItem())));
    }


    public void upload(ActionEvent event) throws IOException {
        Path path = clientFolder.resolve(ta_clientName.getText()).resolve(listViewClient.getSelectionModel().getSelectedItem());
        FilePackage upload = new FilePackage(path);
        GoToNet.sendMessToServer(upload);
        listViewServer.getItems().add(upload.getFileName());
    }



    public void rename(ActionEvent actionEvent) {
        String fileNameToRename = null;
        if (listViewClient.getSelectionModel().getSelectedItem() != null){
            fileNameToRename =listViewClient.getSelectionModel().getSelectedItem();
        } else if (listViewServer.getSelectionModel().getSelectedItem() != null){
            fileNameToRename = listViewServer.getSelectionModel().getSelectedItem();
            } else  {
                LOG.debug("no selected file to rename");
            }
             GoToNet.sendMessToServer(new RequestRename(fileNameToRename,ta_selectedFileName.getText()));
        refreshLists(ta_clientName.getText());
    }

    public void delete(ActionEvent actionEvent) {
        String fileNameToDelete = null;
        char placeWhereDelete = 0;
        if (listViewClient.getSelectionModel().getSelectedItem() != null){
            fileNameToDelete =listViewClient.getSelectionModel().getSelectedItem();
            placeWhereDelete = 'c';
        } else if (listViewServer.getSelectionModel().getSelectedItem() != null){
            fileNameToDelete = listViewServer.getSelectionModel().getSelectedItem();
            placeWhereDelete = 's';
        } else  {
            LOG.debug("no selected file to delete");
        }
        GoToNet.sendMessToServer(new RequestDelete(fileNameToDelete, placeWhereDelete));
        refreshLists(ta_clientName.getText());
    }

    public List<String> refresh (Path folder){
        File folderClient = folder.toFile();
        File [] files = folderClient.listFiles();
        List fileList = new ArrayList();
        if (files.length ==0){
            fileList.add(" ... empty ... ");
        } else {
            for (int i = 0; i < files.length ; i++) {
                fileList.add(files[i].getName());
            }
        }
        return fileList;
    }

    public void refreshLists (String nickName){
        Platform.runLater(()->{
            listViewServer.getItems().clear();
            listViewServer.getItems().addAll(refresh(serverFolder.resolve(nickName)));
        });
            Platform.runLater(() -> {
        listViewClient.getItems().clear();
        listViewClient.getItems().addAll(refresh(clientFolder.resolve(nickName)));


    });
    }
}

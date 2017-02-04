/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roboadvisor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author emesuser001
 */
public class FXMLDocumentController implements Initializable {
     
    @FXML Button optimizerBt;
    
    @FXML
    private void onTreeNodeClick(ActionEvent event) {
        
    }
    
    @FXML
    private void onOptimizedBtClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("PortfolioOptimizer.fxml"));
            Stage curStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Stage newStage = new Stage();
            Scene curScene = new Scene(root);
            newStage.setScene(curScene);
            newStage.setTitle("Testing");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();
            
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

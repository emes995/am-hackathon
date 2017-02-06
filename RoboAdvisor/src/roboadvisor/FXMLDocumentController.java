/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roboadvisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author emesuser001
 */
public class FXMLDocumentController implements Initializable {
     
    @FXML Button optimizerBt;
    @FXML TreeView<String> treeRootNode;
    @FXML TableView<Map> summaryTable;
    
    @FXML
    private void onTreeNodeClick(ActionEvent event) {
        System.err.print("Clicking me");
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
    
    private void drawSummaryTable(String nodeType) {
        InputStream inpStream = getClass().getResourceAsStream("JSON Data/" + nodeType.replace(" ", "_") + "_results.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inpStream));
        JSONParser parser = new JSONParser();
        ObservableList<Map> data = FXCollections.observableArrayList();
        
        try {
            try {
                JSONObject obj = (JSONObject)parser.parse(reader);
                JSONArray array = (JSONArray)obj.get("Summaries");
                Iterator<Object> it = array.iterator();
                JSONObject jAssetObj = (JSONObject) it.next();
                JSONObject jBmkObj = (JSONObject) it.next();
                Map<String, String> dataRow = new HashMap<String,String>();
                dataRow.put("Label", "Market Value");
                dataRow.put("Asset", (String)jAssetObj.get("Market Value").toString());
                dataRow.put("Benchmark", (String)jBmkObj.get("Market Value").toString());
                data.add(dataRow);
                
                dataRow = new HashMap<String,String>();
                dataRow.put("Label", "DV01");
                dataRow.put("Asset", (String)jAssetObj.get("DV01").toString());
                dataRow.put("Benchmark", (String)jBmkObj.get("DV01").toString());
                data.add(dataRow);
                
                dataRow = new HashMap<String,String>();
                dataRow.put("Label", "CR01");
                dataRow.put("Asset", (String)jAssetObj.get("CR01").toString());
                dataRow.put("Benchmark", (String)jBmkObj.get("CR01").toString());
                data.add(dataRow);
                
                dataRow = new HashMap<String,String>();
                dataRow.put("Label", "Avg Rating");
                dataRow.put("Asset", (String)jAssetObj.get("Avg Rating"));
                dataRow.put("Benchmark", (String)jBmkObj.get("Avg Rating"));
                data.add(dataRow);
                
                dataRow = new HashMap<String,String>();
                dataRow.put("Label", "Bmk Type");
                dataRow.put("Asset", (String)jAssetObj.get("Bmk Type"));
                dataRow.put("Benchmark", (String)jBmkObj.get("Bmk Type"));
                data.add(dataRow);
                
                summaryTable.setItems(data);
                                
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void drawDetailsTable(String nodeType) {
        
    }
    
    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            String name = (String) ((TreeItem)treeRootNode.getSelectionModel().getSelectedItem()).getValue();
            System.out.println("Node click: " + name);
            drawSummaryTable(name);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Pair<String,String>> itemsToRead = getTreeNodes();
        Iterator<Pair<String,String>> itrIt = itemsToRead.iterator();
        TreeItem<String> rootNode = new TreeItem<String>("Legal Structure");
        rootNode.setExpanded(true);
        treeRootNode.setRoot(rootNode);
        
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleMouseClicked(event);
        };

        treeRootNode.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle); 

        while(itrIt.hasNext()) {
            Pair<String,String> item = itrIt.next();
            InputStream inpStream = getClass().getResourceAsStream("JSON Data/" + item.getValue());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inpStream));
            JSONParser parser = new JSONParser();
            
            TreeItem<String> leafNode = new TreeItem<String>(item.getKey());
            leafNode.setExpanded(true);
            rootNode.getChildren().add(leafNode);
            
            try {
                try {
                    JSONObject obj = (JSONObject)parser.parse(reader);
                    JSONArray array = (JSONArray)obj.get("Items");
                    Iterator<Object> it = array.iterator();
                    while(it.hasNext()) {
                        JSONObject jObj = (JSONObject) it.next();
                        leafNode.getChildren().add(new TreeItem<String>((String)jObj.get("Name")));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ParseException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        TableColumn<Map, String> lblCol = new TableColumn<>("Type");
        TableColumn<Map, String> keyCol = new TableColumn<>("Asset");
        TableColumn<Map, String> valCol = new TableColumn<>("Benchmark");

        lblCol.setCellValueFactory(new MapValueFactory("Label"));
        lblCol.setMinWidth(100);
        keyCol.setCellValueFactory(new MapValueFactory("Asset"));
        keyCol.setMinWidth(110);
        valCol.setCellValueFactory(new MapValueFactory("Benchmark"));
        valCol.setMinWidth(110);
        
        summaryTable.getColumns().addAll(lblCol, keyCol, valCol);

    }
    
    private ArrayList<Pair<String,String>> getTreeNodes() {
        ArrayList<Pair<String,String>> items = new ArrayList<Pair<String,String>>();
        InputStream inpStream = getClass().getResourceAsStream("JSON Data/TreeItems.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inpStream));
        JSONParser parser = new JSONParser();
        try {
            JSONObject jObj = (JSONObject)parser.parse(reader);
            JSONArray jArr = (JSONArray)jObj.get("TreeItems");
            Iterator<JSONObject> it = jArr.iterator();
            while(it.hasNext()) {
                JSONObject obj = it.next();
                Pair<String,String> item = new Pair<String,String>((String)obj.get("Nodename"), (String)obj.get("Filename"));
                items.add(item);
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return items;
    }
    
}

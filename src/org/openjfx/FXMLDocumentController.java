package org.openjfx;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class FXMLDocumentController implements Initializable {

    @FXML
    private BarChart<String, Integer> isChart;
    @FXML
    private BarChart<String, Integer> ssChart;
    @FXML
    private BarChart<String, Integer> bsChart;


    // SIZE constant is uses to control the
    // array length
    private static final int SIZE = 30;

    // Flags use to help control thread synchronisation 
    private static boolean isSorting = false, ssSorting = false, bsSorting = false;
    private static boolean isDone = false, ssDone = false, bsDone = false;

    // MUTEX objects
    private final Object lockIS = new Object();
    private final Object lockSS = new Object();
    private final Object lockBS = new Object();
    private XYChart.Series<String, Integer> series1;
    private XYChart.Series<String, Integer> series2;
    private XYChart.Series<String, Integer> series3;

    // Sorting arrays
    private Bar[] isArray;
    private Bar[] ssArray;
    private Bar[] bsArray;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        series1 = new XYChart.Series<>();
        series2 = new XYChart.Series<>();
        series3 = new XYChart.Series<>();
        isArray = new Bar[SIZE];
        ssArray = new Bar[SIZE];
        bsArray = new Bar[SIZE];

        // Populating the arrays with shuffled numbers between 1 and 30
            isArray = createShuffledArray(SIZE);
            ssArray = createShuffledArray(SIZE);
            bsArray = createShuffledArray(SIZE);            

        // Create and start the Sorting Threads
        Thread is = new Thread(new InsertionSort(this), "IS Thread");
        Thread ss = new Thread(new SelectionSort(this), "SS Thread");
        Thread bs = new Thread(new BubbleSort(this), "BS Thread");
        is.start();
        ss.start();
        bs.start();

        // Synchronise the main and child threads using the locks
        synchronized (lockIS) {
            updateIsChart();
            isSorting = true;
            lockIS.notify();
        }
        // Synchronise the main and child threads using the locks
        synchronized (lockSS) {
            updateSsChart();
            ssSorting = true;
            lockSS.notify();
        }
        // Synchronise the main and child threads using the locks
        synchronized (lockBS) {
            updateBsChart();
            bsSorting = true;
            lockBS.notify();
        }

        // Determine if the child thread is finished
        if (isDone) {
            try {
                is.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Determine if the child thread is finished
        if (ssDone) {
            try {
                ss.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Determine if the child thread is finished
        if (bsDone) {
            try {
                bs.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Function to create the shuffled array of Bars 
    private Bar [] createShuffledArray(int size) {
        List<Integer> li = new ArrayList<Integer>();
        Bar [] bars = new Bar [size];
        for (Integer n = 0; n<size; n++) {
            li.add(n+1);
        }        
        Collections.shuffle(li);       
        for(int i =0; i <li.size(); i++){
            bars[i] = new Bar(li.get(i),false);
        }
        return bars;
    }

    // Function to update the Insertion Sort Chart 
    public synchronized void updateIsChart() {
        series1.getData().clear();
        XYChart.Data<String, Integer> data;

        // Scan the array for the isLast flag.
        // Change the node's fill color if found
        for (int n = 0; n < isArray.length; n++) {
            data = new XYChart.Data<>(Integer.toString(n + 1), isArray[n].getValue());
            if (isArray[n].isIsLast()) {
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                        newValue.setStyle("-fx-bar-fill: navy;");
                    }
                });
            }
            series1.getData().add(data);
        }
        isChart.getData().setAll(series1);
    }

    // Function to update the Selection Sort Chart     
    public synchronized void updateSsChart() {
        series2.getData().clear();
        XYChart.Data<String, Integer> data;

        // Scan the array for the isLast flag.
        // Change the node's fill color if found        
        for (int n = 0; n < ssArray.length; n++) {
            data = new XYChart.Data<>(Integer.toString(n + 1), ssArray[n].getValue());
            if (ssArray[n].isIsLast()) {
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                        newValue.setStyle("-fx-bar-fill: navy;");
                    }
                });
            }
            series2.getData().add(data);
        }
        ssChart.getData().setAll(series2);
    }

    // Function to update the Bubble Sort Chart     
    public synchronized void updateBsChart() {
        series3.getData().clear();
        XYChart.Data<String, Integer> data;
        
        // Scan the array for the isLast flag.
        // Change the node's fill color if found        
        for (int n = 0; n < bsArray.length; n++) {
            data = new XYChart.Data<>(Integer.toString(n + 1), bsArray[n].getValue());
            if (bsArray[n].isIsLast()) {
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                        newValue.setStyle("-fx-bar-fill: navy;");
                    }
                });
            }
            series3.getData().add(data);
        }
        bsChart.getData().setAll(series3);
    }

    //Getters and Setters
    public Bar[] getIsArray() {
        return isArray;
    }

    public void setIsArray(Bar[] isArray) {
        this.isArray = isArray;
    }

    public Bar[] getSsArray() {
        return ssArray;
    }

    public void setSsArray(Bar[] ssArray) {
        this.ssArray = ssArray;
    }

    public Bar[] getBsArray() {
        return bsArray;
    }

    public void setBsArray(Bar[] bsArray) {
        this.bsArray = bsArray;
    }

    public boolean isIsSorting() {
        return isSorting;
    }

    public void setIsSorting(boolean isSorting) {
        this.isSorting = isSorting;
    }

    public boolean isSsSorting() {
        return ssSorting;
    }

    public void setSsSorting(boolean ssSorting) {
        this.ssSorting = ssSorting;
    }

    public boolean isBsSorting() {
        return bsSorting;
    }

    public void setBsSorting(boolean bsSorting) {
        this.bsSorting = bsSorting;
    }

    public Object getLockIS() {
        return lockIS;
    }

    public Object getLockSS() {
        return lockSS;
    }

    public Object getLockBS() {
        return lockBS;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public void setSsDone(boolean ssDone) {
        this.ssDone = ssDone;
    }

    public void setBsDone(boolean bsDone) {
        this.bsDone = bsDone;
    }

}

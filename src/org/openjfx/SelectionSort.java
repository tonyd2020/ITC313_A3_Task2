/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openjfx;

import javafx.application.Platform;

/**
 *
 * @author Tony RMIT
 */
public class SelectionSort implements Runnable {

    FXMLDocumentController fxml;
    Bar[] ra;

    public SelectionSort(FXMLDocumentController fxml) {
        this.fxml = fxml;

    }

    @Override
    public void run() {
        synchronized (fxml.getLockSS()) {
            while (!fxml.isSsSorting()) {
                try {
                    fxml.getLockSS().wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            this.ra = fxml.getSsArray();
            for (int i = 0; i < ra.length - 1; i++) {
                Bar currentMin = ra[i];
                int currentMinIndex = i;
                int k = 0;
                for (int j = i + 1; j < ra.length; j++) {
                    //Compare adjacent elements
                    if (currentMin.getValue() > ra[j].getValue()) {
                        currentMin = ra[j];
                        currentMinIndex = j;
                        k = j;
                    }
                }
                if (currentMinIndex != i) {
                    ra[currentMinIndex] = ra[i];
                    ra[i] = currentMin;
                }
                // Scan the array and set the isLast flag when the element index is 
                // the same as the tagged element                
                for (int n = 0; n < ra.length; n++) {
                    if (n == currentMinIndex) {
                        ra[n].setIsLast(true);
                    } else {
                        ra[n].setIsLast(false);
                    }
                }
                // Sleep for 800ms and call-back the controller
                // function redraw the chart using the runLater command
                // to utilise the main thread.                
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } finally {
                    Platform.runLater(() -> {
                        fxml.updateSsChart();
                    });
                }
            }
            // Set the flags and notify lock release to enable the 
            // main controller maintain operation
            // and prevent deadlock.          
            fxml.setSsSorting(false);
            fxml.setSsDone(true);
            fxml.getLockSS().notify();
        }
    }

}

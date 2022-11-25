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
public class BubbleSort implements Runnable {

    FXMLDocumentController fxml;

    Bar[] ra;

    public BubbleSort(FXMLDocumentController fxml) {
        this.fxml = fxml;
    }

    @Override
    public void run() {

        synchronized (fxml.getLockBS()) {
            while (!fxml.isBsSorting()) {
                try {
                    fxml.getLockBS().wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            this.ra = fxml.getBsArray();
            boolean nextPass = true;
            for (int k = 1; k < ra.length && nextPass; k++) {
                nextPass = false;
                int g=0;
                for (int i = 0; i < ra.length - k; i++) {
                    //Compare adjacent elements
                    if (ra[i].getValue() > ra[i + 1].getValue()) {
                        // Swap if current element has higher value
                        Bar temp = ra[i];
                        ra[i] = ra[i + 1];
                        ra[i + 1] = temp;
                        nextPass = true;
                    }
                    // Tag the last element selected before the nextPass flag
                    // is set to remain false
                    if(!nextPass){
                        g=i;
                    }
                }
                // Scan the array and set the isLast flag when the element index is 
                // the same as the tagged element
                for (int n = 0; n < ra.length; n++) {
                    if (n == g) {
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
                        fxml.updateBsChart();
                    });
                }
            }
            // Set the flags and notify lock release to enable the 
            // main controller maintain operation
            // and prevent deadlock.
            fxml.setBsSorting(false);
            fxml.setBsDone(true);
            fxml.getLockBS().notify();            
        }

    }
}

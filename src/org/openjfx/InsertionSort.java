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
public class InsertionSort implements Runnable {

    Bar[] ra;
    FXMLDocumentController fxml;

    public InsertionSort(FXMLDocumentController fxml) {
        this.fxml = fxml;
    }

    @Override
    public void run() {
        //Use a separate MUTEX lock to enforce synchronisation
        synchronized (fxml.getLockIS()) {
            while (!fxml.isIsSorting()) {
                try {
                    fxml.getLockIS().wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            this.ra = this.fxml.getIsArray();
            for (int i = 1; i < ra.length; i++) {
                Bar currentElement = ra[i];
                currentElement.setIsLast(true);
                int k;
                for (k = i - 1; k >= 0 && ra[k].getValue() > currentElement.getValue(); k--) {
                    ra[k + 1] = ra[k];
                }
                ra[k + 1] = currentElement;

                //Mark the last element of the sorted sub-array

                for (int n = 0; n < ra.length; n++) {
                    if (n == k+1) {
                        ra[n].setIsLast(true);
                    } else {
                        ra[n].setIsLast(false);
                    }
                }
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } finally {

                    //Use the main thread to update the GUI
                    Platform.runLater(() -> {
                        fxml.updateIsChart();
                    });
                }
            }
            // Set the flags and notify lock release to enable the 
            // main controller maintain operation
            // and prevent deadlock.            
            fxml.setIsSorting(false);
            fxml.setIsDone(true);
            fxml.getLockIS().notify();
        }

    }
}

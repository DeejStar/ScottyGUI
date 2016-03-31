/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

/**
 *
 * @author Administrator
 */
public class ThreadExample {

    public static void main() {

        new Thread("PutTheadName") {
            @Override
            public void run() {
// Thread code goes here.
            }
        }.start();

    }

    private ThreadExample() {
    }
}

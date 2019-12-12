package com.steve.hdc;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;

public class ClientGui {
    private JButton sendButton;
    private JTextField sendMessageBox;
    private JTextPane recvMessagePane;
    private JPanel panel1;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField recipientField;
    private JButton signUpButton;
    private JButton getFileButton;
    private JTextField fileNameField;
    private JButton recieveButton;

    public ClientGui(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String recipient = recipientField.getText();
                String toTextBox = null;

                String response = null;

                try{
                    response = Client.sendMsg(username, password, new Message(username, recipient, sendMessageBox.getText()));
                }
                catch (Exception ex){
                    System.err.println(ex);
                }

                if(response == null){
                    toTextBox = username + ": " + sendMessageBox.getText();
                }
                else{
                    toTextBox = response;
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                Document sendDoc = sendMessageBox.getDocument();
                try {
                    sendDoc.remove(0,sendDoc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String toTextBox = null;

                String response;

                response = Client.signup(username, password);

                if(response == null){
                    toTextBox = username + " signed up.";
                }
                else{
                    toTextBox = response;
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        });

        getFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String filename = fileNameField.getText();
                String toTextBox;

                Message response = Client.getFile(username, password, filename);

                if(response != null){
                    toTextBox = response.getContent().toString();
                }
                else {
                    toTextBox = "File Retrieval Failed";
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        });

        recieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();

                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                Message[] messages = Client.getMsg(username, password, currentTime.getTime());

                Document doc = recvMessagePane.getDocument();

                if(messages != null){
                    for(Message msg : messages){
                        try {
                            System.out.println(msg.toJSON());
                            JSONObject obj = new JSONObject(msg.toJSON());
                            String toInsert = obj.getString("reciever") + ": " + obj.get("content");
                            doc.insertString(doc.getLength(),toInsert + "\n",null);
                        } catch (BadLocationException ble) {
                            ble.printStackTrace();
                        }
                    }
                }
            }
        });
        //getMessages();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hadoop Distributed Chat Client");
        frame.setContentPane(new ClientGui().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void getMessages(){
        String username = usernameField.getText();
        char[] passwordArray = passwordField.getPassword();
        String password = passwordArray.toString();

        if(username != null && password != null){
            while(true){
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                Message[] messages = Client.getMsg(username, password, currentTime.getTime());

                Document doc = recvMessagePane.getDocument();

                if(messages != null){
                    for(Message msg : messages){
                        try {
                            doc.insertString(doc.getLength(),msg.getContent().toString() + "\n",null);
                        } catch (BadLocationException ble) {
                            ble.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//end while loop
        }
    }
}



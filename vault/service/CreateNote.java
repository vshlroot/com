package com.vault.service;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by vshlroot on 4/15/2016.
 */
/*
Creates a note as an XML.
 */
public class CreateNote {

    private String pathSeparator=File.separator;
    private long docId;
    private String absolutePath;

    public CreateNote(){
        docId=GlobalVariables.getNextDocId();
        absolutePath=getAbsolutePath();
    }

    public String getAbsolutePath(){
        // Fetching the root directory path

        Calendar cal=Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);

        String absPath=GlobalVariables.getRootDirectoryPath()+pathSeparator+year;

        File tempFile=new File(absPath);
        if(!tempFile.exists()){
            new File(absPath).mkdir();
        }

        absPath=absPath+pathSeparator+month;
        tempFile=new File(absPath);
        if(!tempFile.exists()){
            new File(absPath).mkdir();
        }

        absPath=absPath+pathSeparator+docId+".xml";
        return absPath;
    }

    public void createXMLNote (NoteBean noteBean) throws NullPointerException{
        if(noteBean==null) {
            System.out.println("Received Null in NoteBean: Exit");
            throw new NullPointerException("noteBean is Empty");
        }

        try {
            DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder =dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            // root element
            Element rootElement = doc.createElement("Note");
            doc.appendChild(rootElement);

            //  supercars element
            Element docIdElement = doc.createElement("DocId");
            docIdElement.setTextContent(""+docId);
            rootElement.appendChild(docIdElement);

            Element titleElement = doc.createElement("Title");
            CDATASection titleData=doc.createCDATASection(noteBean.getTitle());
            titleElement.appendChild(titleData);
            rootElement.appendChild(titleElement);

            Element priorityElement = doc.createElement("Priority");
            priorityElement.setTextContent("" + noteBean.getPriority());
            rootElement.appendChild(priorityElement);

            Element contentElement = doc.createElement("Content");
            CDATASection contentData=doc.createCDATASection(noteBean.getContent());
            contentElement.appendChild(contentData);
            rootElement.appendChild(contentElement);

            ArrayList<String> tempContentArrayList=noteBean.getTags();
            CDATASection tempCDATA;
            Element tagsElement = doc.createElement("Tags");
            Element tempTagElement;
            CDATASection tempTagData;
            for (int i = 0; i < tempContentArrayList.size(); i++) {
                tempTagElement = doc.createElement("Tag");
                tempTagData=doc.createCDATASection(tempContentArrayList.get(i));
                tempTagElement.appendChild(tempTagData);
                tagsElement.appendChild(tempTagElement);
            }
            rootElement.appendChild(tagsElement);


            Element referencesElement = doc.createElement("References");
            Element tempReferenceElement;
            CDATASection tempReferenceData;
            tempContentArrayList=noteBean.getReferences();
            for (int i = 0; i < tempContentArrayList.size(); i++) {
                tempReferenceElement= doc.createElement("Reference");
                tempReferenceData=doc.createCDATASection(tempContentArrayList.get(i));
                tempReferenceElement.appendChild(tempReferenceData);
                referencesElement.appendChild(tempReferenceElement);
            }
            rootElement.appendChild(referencesElement);


            Element videosElement = doc.createElement("Videos");
            Element tempVideosElement;
            CDATASection tempVideoData;
            tempContentArrayList =noteBean.getVideos();
            System.out.println(tempContentArrayList.size());
            for (int i = 0; i < tempContentArrayList.size(); i++) {
                tempVideosElement= doc.createElement("Video");
                tempVideoData=doc.createCDATASection(tempContentArrayList.get(i));
                tempVideosElement.appendChild(tempVideoData);
                videosElement.appendChild(tempVideosElement);
            }
            rootElement.appendChild(videosElement);

            // write the content into xml file
            saveXML(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding
    }

    public void saveXML(Document doc){
        try{
            TransformerFactory transformerFactory =TransformerFactory.newInstance();
            Transformer transformer =transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);

            System.out.println(absolutePath);
            StreamResult result =new StreamResult(new File(absolutePath));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult =new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            GlobalVariables.populateGlobalVariables("\\2012\\2\\");
            CreateNote createNote = new CreateNote();
            NoteBean noteBean = GetNote.getNote("\\2012\\2\\234.xml");
            createNote.createXMLNote(noteBean);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

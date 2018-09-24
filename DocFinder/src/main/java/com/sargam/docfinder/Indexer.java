/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sargam.docfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author anoop
 */
public class Indexer {

  private IndexWriter writer;
  private String indexDirectoryPath;

  public Indexer(String indexDirectoryPath) throws IOException {
    this.indexDirectoryPath = indexDirectoryPath;
  }

  private IndexWriter getIndexWriter() throws IOException {
    if (writer == null) {
      //this directory will contain the indexes
      Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));

      //create the indexer
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      writer = new IndexWriter(indexDirectory, iwc);
    }
    return writer;
  }

  public void close() throws CorruptIndexException, IOException {
    getIndexWriter().close();
  }

  private Document getDocument(Path file) throws IOException {
    Document document = new Document();

    Metadata metadata = new Metadata();
    StringBuilder fileContent = null;
    try {
      fileContent = new StringBuilder(new Tika().parseToString(file));
    } catch (TikaException ex) {
      Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
    }
    Reader content = new Tika().parse(new FileInputStream(file.toFile()), metadata);

    //index file contents
    //  Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file));
    //InputStream stream = Files.newInputStream(file);
    //document.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
    document.add(new TextField(SystemConstant.DOC_CONTENTS, content));

    //index file name    
    Field filePathField = new StringField(SystemConstant.DOC_PATH, file.toString(), Field.Store.YES);
    document.add(filePathField);

    Field fileNameField = new TextField(SystemConstant.DOC_NAME, file.getFileName().toString().toLowerCase(), Field.Store.YES);
    document.add(fileNameField);

    document.add(new TextField(SystemConstant.SKILL, getSkills(fileContent.toString().toLowerCase()), Field.Store.YES));

    return document;
  }

  private void indexFile(File file) throws IOException, ParseException {
    System.out.println("Indexing " + file.getName());
    Document document = getDocument(Paths.get(file.getCanonicalPath()));    
    getIndexWriter().addDocument(document);
  }

  public int createIndex(String dataDirPath, FileFilter filter)
          throws IOException, ParseException {
    //get all files in the data directory
    File[] files = new File(dataDirPath).listFiles();
    //getIndexWriter().deleteAll();
    for (File file : files) {
      if (!file.isDirectory()
              && !file.isHidden()
              && file.exists()
              && file.canRead()
              && filter.accept(file)) {
        indexFile(file);
      }
    }
    int indexedCount = getIndexWriter().numDocs();
    close();
    return indexedCount;
  }

  private String getSkills(String content) {
    StringBuilder skills = new StringBuilder();
    if (content != null) {
      for (String skill : SystemConstant.SKILLS) {
        if (content.contains(skill)) {
          if (skills.length() == 0) {
            skills.append(skill);
          } else {
            skills.append(" ").append(skill);
          }
        }
      }
    }
    return skills.toString();
  }

  private void removeDocument(Path path) {

  }

  private void updateDocument(Path path) {

  }
}

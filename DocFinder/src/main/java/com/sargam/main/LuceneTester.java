/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sargam.main;

import com.sargam.docfinder.Indexer;
import com.sargam.docfinder.Searcher;
import com.sargam.docfinder.SystemConstant;
import com.sargam.docfinder.TextFileFilter;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author anoop
 */
public class LuceneTester {

  String indexDir = "/home/java-2/lucene/Index";
  String dataDir = "/home/java-2/lucene/Data";
  Indexer indexer;
  Searcher searcher;

  public static void main(String[] args) throws ParseException {
    LuceneTester tester;
    try {
      tester = new LuceneTester();
      //tester.createIndex();
      tester.search("skills:VB.net");
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void createIndex() throws IOException, ParseException {
    indexer = new Indexer(indexDir);
    int numIndexed;
    long startTime = System.currentTimeMillis();
    numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
    long endTime = System.currentTimeMillis();
    indexer.close();
    System.out.println(numIndexed + " File indexed, time taken: "
            + (endTime - startTime) + " ms");
  }

  private void search(String searchQuery) throws IOException, ParseException {
    searcher = new Searcher(indexDir);
    long startTime = System.currentTimeMillis();
    TopDocs hits = searcher.search(searchQuery);
    long endTime = System.currentTimeMillis();

    System.out.println(hits.totalHits
            + " documents found. Time :" + (endTime - startTime));
    for (ScoreDoc scoreDoc : hits.scoreDocs) {
      Document doc = searcher.getDocument(scoreDoc);
      System.out.println("File: " + doc.get(SystemConstant.DOC_NAME));
    }
  }
}

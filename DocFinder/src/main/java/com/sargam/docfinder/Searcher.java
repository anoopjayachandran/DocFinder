/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sargam.docfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author anoop
 */
public class Searcher {

  IndexSearcher indexSearcher;
  QueryParser queryParser;
  Query query;

  public Searcher(String indexDirectoryPath)
          throws IOException {

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectoryPath)));
    indexSearcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer();

    queryParser = new QueryParser(SystemConstant.SKILL, analyzer);

  }

  public TopDocs search(String searchQuery)
          throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    //int count = indexSearcher.count(query);
    return indexSearcher.search(query, SystemConstant.MAX_SEARCH);
  }

  public Document getDocument(ScoreDoc scoreDoc)
          throws CorruptIndexException, IOException {
    return indexSearcher.doc(scoreDoc.doc);
  }

}

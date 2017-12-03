package controller;

import dao.BookDaoImpl;
import model.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Index {

    /**
     * 创建索引库
     *
     * @throws IOException
     */
    @Test
    public void creatIndex() throws IOException {
        BookDaoImpl bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.getBookList();
//        System.out.printf("大小"+bookList.size());
        List<Document> documentList = new ArrayList<>();
        for (Book book :
                bookList) {
            Document document = new Document();
            Field fieldId = new StringField("id", book.getId().toString(), Field.Store.YES);
            Field fieldName = new TextField("name", book.getName(), Field.Store.YES);
            Field fieldPrice = new FloatField("price", book.getPrice(), Field.Store.YES);
            Field fieldPic = new StoredField("pic", book.getPic());
            Field description = new TextField("description", book.getDescription(), Field.Store.NO);
            document.add(fieldId);
            document.add(fieldName);
            document.add(fieldPrice);
            document.add(fieldPic);
            document.add(description);
            documentList.add(document);
        }
        Analyzer analyzer = new IKAnalyzer();
        File file = new File("E://ideat//");
        Directory d = FSDirectory.open(file);
        IndexWriterConfig c = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        IndexWriter indexWriter = new IndexWriter(d, c);
        for (Document doc :
                documentList) {
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }

    /**
     * 进行索引查询
     *
     * @throws ParseException
     * @throws IOException
     */
    @Test
    public void serache() throws ParseException, IOException {
        QueryParser parser = new QueryParser("name", new IKAnalyzer());
        Query parse = parser.parse("description:java AND lucene");
        File files = new File("E://ideat//");
        Directory file = FSDirectory.open(files);
        IndexReader reder = DirectoryReader.open(file);
        IndexSearcher indexSearcher = new IndexSearcher(reder);
        TopDocs topDocs = indexSearcher.search(parse, 10);
        int totalHits = topDocs.totalHits;
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc sc :
                scoreDocs) {
            int doc = sc.doc;
            Document doc1 = indexSearcher.doc(doc);
            System.out.println(doc1.get("name"));
        }
    }

    @Test
    public void delectIndex() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        File file = new File("E://ideat//");
        Directory directory = FSDirectory.open(file);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexWriter.deleteDocuments(new Term("id", "1"));
        indexWriter.deleteAll();
        indexWriter.close();
    }

    @Test
    public void updateIndex() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        File file = new File("E://ideat//");
        Directory directory = FSDirectory.open(file);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document doc = new Document();
        doc.add(new TextField("id","hhaha", Field.Store.YES));
        indexWriter.updateDocument(new Term("id","2"),doc);
        indexWriter.close();
    }

}

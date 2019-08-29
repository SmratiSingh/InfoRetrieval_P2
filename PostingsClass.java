import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class PostingsClass {
	static HashMap<String, LinkedList<Integer>> h = new HashMap<String, LinkedList<Integer>>();
	
	public static void TaatAND(HashMap<String, LinkedList<Integer>> h, String[] termsArray, FileWriter filewriter)
			throws IOException {

		LinkedList<Integer> result = new LinkedList<Integer>();
		LinkedList<Integer> t = new LinkedList<Integer>();
		LinkedList<Integer> t2 = new LinkedList<Integer>();
		LinkedList<Integer> filteredPosting = new LinkedList<Integer>();
		result = h.get(termsArray[0]);
		LinkedList<Integer> partialResult = new LinkedList<Integer>();
		partialResult.addAll(result);

		int comp = 0;
		for (int x = 1; x < termsArray.length; x++) {

			int i = 0, j = 0, a = 0, b = 0;

			if (!(termsArray[x].equals("\\s\\s+"))) {
				filteredPosting = h.get(termsArray[x]);

				while (i < filteredPosting.size() && j < partialResult.size()) {

					if (filteredPosting.get(i).equals(partialResult.get(j))) {
						t.add(filteredPosting.get(i));
						i++;
						j++;
						comp++;

					} else if (filteredPosting.get(i) < partialResult.get(j)) {
						i++;
						comp++;

					} else {
						j++;
						comp++;

					}

				}

				while (a < t.size() && b < partialResult.size()) {
					if (t.get(a).equals(partialResult.get(b))) {
						t2.add(t.get(a));
						a++;
						b++;

					} else if (t.get(a) < partialResult.get(b)) {
						a++;
					} else {
						b++;
					}

				}

			}
			partialResult.removeAll(partialResult);
			partialResult.addAll(t2);

		}
		
		filewriter.write("TaatAND" + System.getProperty("line.separator"));

		for (int i = 0; i < termsArray.length; i++) {
			filewriter.write(termsArray[i] + " ");

		}
		
		filewriter.write(System.getProperty("line.separator"));
		if (partialResult.size() > 0) {
			filewriter.write("Result: ");
			for (int i = 0; i < partialResult.size(); i++) {
				filewriter.write(partialResult.get(i) + " ");
			}
		} else {
			filewriter.write("Results: empty");
		}
		filewriter.write(System.getProperty("line.separator") + "Number of documents in results: " + partialResult.size());
		filewriter.write(System.getProperty("line.separator") + "Number of comparisions: " + comp);

	}
	public static void TaatOR(HashMap<String, LinkedList<Integer>> h, String[] termsArray, FileWriter filewriter)
			throws IOException {
		LinkedList<Integer> result2 = new LinkedList<Integer>();
		LinkedList<Integer> t = new LinkedList<Integer>();
		LinkedList<Integer> finalList = new LinkedList<Integer>();
		SortedSet<Integer> ResultSet = new TreeSet<>();

		result2 = h.get(termsArray[0]);
		ResultSet.addAll(result2);
		int comp = 0;
		for (int x = 1; x < termsArray.length; x++) {

			int i = 0, j = 0;

			if (!(termsArray[x].equals("\\s\\s+"))) {

				LinkedList<Integer> initialList = h.get(termsArray[x]);

				while (i < initialList.size() && j < result2.size()) {
					if (initialList.get(i).equals(result2.get(j))) {

						t.add(initialList.get(i));
						i++;
						j++;
						comp++;

					} else if (initialList.get(i) < result2.get(j)) {

						t.add(initialList.get(i));
						i++;
						comp++;
					} else {

						t.add(result2.get(j));
						j++;
						comp++;

					}

				}
				while (i < initialList.size()) {
					t.add(initialList.get(i));
					i++;
				}
				while (j < result2.size()) {
					t.add(result2.get(j));
					j++;
				}
			}

			ResultSet.addAll(ResultSet);
			ResultSet.addAll(t);

		}
		finalList.addAll(ResultSet);
		filewriter.write(System.getProperty("line.separator") + "TaatOr" + System.getProperty("line.separator"));
		for (int i = 0; i < termsArray.length; i++) {
			filewriter.write(termsArray[i] + " ");

		}
		
		filewriter.write(System.getProperty("line.separator"));
		if (finalList.size() > 0) {
			filewriter.write("Results: ");
			for (int i = 0; i < finalList.size(); i++) {
				filewriter.write(finalList.get(i) + " ");
			}
		} else {
			filewriter.write("Results: empty ");
		}
		filewriter.write(System.getProperty("line.separator") + "Number of documents in results: " + finalList.size());
		filewriter.write(System.getProperty("line.separator") + "Number of comparisions: " + comp);

	}

	private static void DaatAND(HashMap<String, LinkedList<Integer>> h, String[] termsArray, FileWriter filewriter)
			throws IOException {
		boolean flag = true;
		java.util.List<LinkedList<Integer>> listOfDocs = new ArrayList<>();
		LinkedList<Integer> initialList = new LinkedList<>();
		LinkedList<Integer> compList;
		LinkedList<Integer> resultAnd = new LinkedList<>();
		int currentPointer[] = new int[termsArray.length];
		int sizeLOD[] = new int[termsArray.length];
		int i = 0, compCount = 0;
		int scoreDoc = 0, minValue = 0;
		for (i = 0; i < termsArray.length; i++) {
			initialList = h.get(termsArray[i]);
			listOfDocs.add(initialList);
			sizeLOD[i] = initialList.size();
		}

		for (int j : currentPointer) {
			currentPointer[j] = 0;
		}
		while (flag) {
			scoreDoc = 0;
			compList = new LinkedList<>();
			for (i = 0; i < termsArray.length; i++) {
				compList.add((listOfDocs.get(i).get(currentPointer[i])));

			}

			minValue = Collections.min(compList);
			for (i = 0; i < compList.size(); i++) {
				if (compList.get(i).equals(minValue)) {
					currentPointer[i]++;
					scoreDoc++;
				}
				compCount++;

			}
			if (scoreDoc == termsArray.length) {
				resultAnd.add(minValue);
			}
			for (i = 0; i < currentPointer.length; i++) {
				if (sizeLOD[i] <= currentPointer[i]) {
					flag = false;
					break;
				}
			}

		}

		
		filewriter.write(System.getProperty("line.separator") + "DaatAND" + System.getProperty("line.separator"));
		for (i = 0; i < termsArray.length; i++) {
			filewriter.write(termsArray[i] + " ");

		}
		filewriter.write(System.getProperty("line.separator"));
		if (resultAnd.size() > 0) {
			filewriter.write("Result: ");
			for (i = 0; i < resultAnd.size(); i++) {
				filewriter.write(resultAnd.get(i) + " ");
			}
		} else {
			filewriter.write("Results: empty");
		}
		filewriter.write(System.getProperty("line.separator") + "Number of documents in results: " + resultAnd.size());
		filewriter.write(System.getProperty("line.separator") + "Number of comparisions: " + compCount);
	}

	private static void DaatOR(HashMap<String, LinkedList<Integer>> h, String[] termsArray, FileWriter filewriter)
			throws IOException {
		boolean flag = true;
		java.util.List<LinkedList<Integer>> listOfDocs = new ArrayList<>();
		LinkedList<Integer> initialList = new LinkedList<>();
		LinkedList<Integer> compList;
		LinkedList<Integer> resultOr = new LinkedList<>();
		int currentPointer[] = new int[termsArray.length];
		int sizeLOD[] = new int[termsArray.length];
		int i = 0, compCountOr = 0;
		int scoreDoc = 0, minValue = 0;

		for (i = 0; i < termsArray.length; i++) {
			initialList = h.get(termsArray[i]);
			listOfDocs.add(initialList);
			sizeLOD[i] = initialList.size();
		}

		for (int j : currentPointer) {
			currentPointer[j] = 0;
		}
		while (flag) {
			scoreDoc = 0;
			compList = new LinkedList<>();
			for (i = 0; i < termsArray.length; i++) {
				compList.add((listOfDocs.get(i).get(currentPointer[i])));

			}

			minValue = Collections.min(compList);
			for (i = 0; i < compList.size(); i++) {
				if (compList.get(i).equals(minValue)) {
					currentPointer[i]++;
					scoreDoc++;
				}
				compCountOr++;

			}
			if (scoreDoc > 0) {
				resultOr.add(minValue);

			}

			for (i = 0; i < currentPointer.length; i++) {
				if (sizeLOD[i] <= currentPointer[i]) {
					flag = false;
					break;
				}
			}

		}

		filewriter.write(System.getProperty("line.separator") + "DaatOr" + System.getProperty("line.separator"));
		for (i = 0; i < termsArray.length; i++) {
			filewriter.write(termsArray[i] + " ");
		}
		filewriter.write(System.getProperty("line.separator"));
		if (resultOr.size() > 0) {
			filewriter.write("Result: ");
			for (i = 0; i < resultOr.size(); i++) {
				filewriter.write(resultOr.get(i) + " ");
			}
		} else {
			filewriter.write("Results: empty");
		}
		filewriter.write(System.getProperty("line.separator") + "Number of documents in results: " + resultOr.size());
		filewriter.write(System.getProperty("line.separator") + "Number of comparisions: " + compCountOr);
		filewriter.write(System.getProperty("line.separator"));
	}


	public static void main(String args[]) throws IOException {
		
		String termsArray[] = new String[60];
		String outputfile = args[1];
		FileWriter filewriter = new FileWriter(outputfile, true);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]), "UTF8"));
		String line;

		// hashmap generation
		Path path = Paths.get(args[0]);
		Directory indexDirectory;
		try {

			indexDirectory = FSDirectory.open(path);
			DirectoryReader directoryReader = DirectoryReader.open(indexDirectory);
			IndexReader luceneIReader = directoryReader;
			String[] TfieldsArray = new String[] { "text_en", "text_es", "text_fr" };

			for (String fields : TfieldsArray) {

				Terms terms = MultiFields.getTerms(directoryReader, fields);
				TermsEnum Tenum = terms.iterator();

				BytesRef term = Tenum.next();
				while (term != null) {
					String termString = term.utf8ToString();
					PostingsEnum Penum = MultiFields.getTermDocsEnum(luceneIReader, fields, term);
					int docID = Penum.nextDoc();
					while (docID != PostingsEnum.NO_MORE_DOCS) {
						if (PostingsClass.h.get(termString) == null) {
							LinkedList<Integer> L = new LinkedList<Integer>();
							L.add(docID);
							PostingsClass.h.put(termString, L);
						} else {
							PostingsClass.h.get(termString).add(docID);
						}
						docID = Penum.nextDoc();
					}
					term = Tenum.next();
				}

			}

			
			// Get Postings
			while ((line = br.readLine()) != null) { 
				String t_arr = line.trim();
				termsArray = t_arr.split("\\s");
				for (int x = 0; x < termsArray.length; x++) {
					if (!(termsArray[x].equals("\\s\\s+"))) { 
						LinkedList<Integer> initialList = h.get(termsArray[x]);						
						filewriter.write("GetPostings" + System.getProperty("line.separator") + termsArray[x]);
						filewriter.write(System.getProperty("line.separator") + "Postings list: ");
						for (int i = 0; i < initialList.size(); i++) {
							filewriter.write(initialList.get(i) + " ");}
						filewriter.write(System.getProperty("line.separator") );
					}}
               //calling
				TaatAND(h, termsArray, filewriter);
				TaatOR(h, termsArray, filewriter);
				DaatAND(h, termsArray, filewriter);
				DaatOR(h, termsArray, filewriter);
			}
           
			filewriter.close();
			
		}

		catch (IOException e) {

		}

	}
}

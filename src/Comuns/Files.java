package Comuns;

import java.io.File;
import java.util.ArrayList;

public class Files {

	//Obter lista de ficheiros numa pasta
	public ArrayList<FileDetails> getFilesList(File folder) {
		File filesFolder = new File("./" + folder);
		File[] filesArray = filesFolder.listFiles();

		ArrayList<FileDetails> filesList = new ArrayList<FileDetails>();

		for (int i = 0; i < filesArray.length; i++) {
			if (filesArray[i].isFile()) {
				FileDetails _file = new FileDetails(filesArray[i].getName(), 10);
				filesList.add(_file);
			}
		}

		return filesList;

	}

}

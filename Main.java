package fileManager;

public class Main {
	 public static void main(String[] args) {
	        FileManager fm = new FileManager();
	        fm.addFile("docs/readme.txt", "This is a README file.");
	        fm.addFile("src/main/java/Main.java", "public class Main { }");
	        fm.createDirectory("docs");

	        fm.displayFileContent("docs/readme.txt");
	        fm.displayDirectory("docs");
	        fm.displayDirectory("src/main/java");

	        fm.recordChanges("Initial commit");

	        fm.integrateChangesFromBranch("feature-branch");

	        fm.addFile("docs/changes.txt", "Some changes");
	        fm.recordChanges("Added changes");

	        System.out.println("Change Log:");
	        for (String change : fm.getChangeLog()) {
	            System.out.println(change);
	        }
	    }
	

}

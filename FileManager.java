package fileManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileManager {
    private Map<String, Map<String, String>> fileData;
    private Map<String, List<String>> directories;
    private List<String> changeLog;

    public FileManager() {
        fileData = new ConcurrentHashMap<>();
        directories = new ConcurrentHashMap<>();
        directories.put("", new CopyOnWriteArrayList<>()); // Root directory
        changeLog = new CopyOnWriteArrayList<>();
    }

    public void addFile(String filePath, String content) {
        String[] parts = filePath.split("/");
        String folderPath = "";
        for (int i = 0; i < parts.length - 1; i++) {
            folderPath += parts[i] + "/";
            directories.putIfAbsent(folderPath, new CopyOnWriteArrayList<>());
        }
        String fileName = parts[parts.length - 1];
        String folder = folderPath;
        directories.computeIfAbsent(folder, k -> new CopyOnWriteArrayList<>()).add(fileName);
        Map<String, String> versionHistory = new ConcurrentHashMap<>();
        versionHistory.put("1", content);
        fileData.put(filePath, versionHistory);
    }

    public void displayFileContent(String filePath) {
        Map<String, String> versionHistory = fileData.get(filePath);
        if (versionHistory != null) {
            System.out.println("File: " + filePath);
            System.out.println("Content:");
            for (Map.Entry<String, String> entry : versionHistory.entrySet()) {
                System.out.println("Version " + entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("File not found: " + filePath);
        }
    }

    public void createDirectory(String directoryPath) {
        directories.putIfAbsent(directoryPath, new CopyOnWriteArrayList<>());
    }

    public void displayDirectory(String directoryPath) {
        List<String> files = directories.get(directoryPath);
        if (files != null) {
            System.out.println("Directory: " + directoryPath);
            System.out.println("Files:");
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("Directory not found: " + directoryPath);
        }
    }

    public void recordChanges(String message) {
        changeLog.add(message);
    }

    public void integrateChangesFromBranch(String branchName) {
        
    }

    public List<String> getChangeLog() {
        return changeLog;
    }
    
    public void fetchUpdates(Map<String, Map<String, String>> remoteFileData) {
        for (Map.Entry<String, Map<String, String>> entry : remoteFileData.entrySet()) {
            String filePath = entry.getKey();
            Map<String, String> remoteVersionHistory = entry.getValue();
            Map<String, String> localVersionHistory = fileData.get(filePath);
            if (localVersionHistory == null) {
                fileData.put(filePath, new ConcurrentHashMap<>(remoteVersionHistory));
            } else {
                mergeChanges(filePath, localVersionHistory, remoteVersionHistory);
            }
        }
    }
    
    private void mergeChanges(String filePath, Map<String, String> localVersionHistory, Map<String, String> remoteVersionHistory) {
        Map<String, String> mergedVersionHistory = new ConcurrentHashMap<>();
        
        Set<String> localVersions = localVersionHistory.keySet();
        Set<String> remoteVersions = remoteVersionHistory.keySet();

        Set<String> commonVersions = new HashSet<>(localVersions);
        commonVersions.retainAll(remoteVersions);

        for (String version : commonVersions) {
            String localContent = localVersionHistory.get(version);
            String remoteContent = remoteVersionHistory.get(version);

            if (localContent.equals(remoteContent)) {
                mergedVersionHistory.put(version, localContent);
            } else {
                mergedVersionHistory.put(version, remoteContent);
            }
        }

        for (String version : localVersions) {
            if (!commonVersions.contains(version)) {
                mergedVersionHistory.put(version, localVersionHistory.get(version));
            }
        }

        for (String version : remoteVersions) {
            if (!commonVersions.contains(version) && !localVersions.contains(version)) {
                mergedVersionHistory.put(version, remoteVersionHistory.get(version));
            }
        }

        fileData.put(filePath, mergedVersionHistory);
    }
	
	

}

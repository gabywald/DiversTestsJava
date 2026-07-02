package gabywald.terminal2;

import java.util.*;

/**
 * Classe représentant un système de fichiers simplifié pour l'émulateur de terminal.
 * Gère les fichiers et répertoires sous forme de structure arborescente.
 */
public class FileSystem {
    private static final String ROOT = "/";
    private String currentDirectory;
    private Map<String, Set<String>> directories;
    private Map<String, String> files;

    /**
     * Constructeur : initialise le système de fichiers avec un répertoire racine.
     */
    public FileSystem() {
        this.currentDirectory = ROOT;
        this.directories = new HashMap<>();
        this.files = new HashMap<>();
        this.directories.put(ROOT, new HashSet<>());
    }

    /**
     * Retourne le répertoire racine.
     */
    public String getRoot() {
        return ROOT;
    }

    /**
     * Retourne le répertoire courant.
     */
    public String getCurrentDirectory() {
        return this.currentDirectory;
    }

    /**
     * Change le répertoire courant.
     * @param path Chemin absolu du nouveau répertoire courant.
     * @throws IllegalArgumentException Si le répertoire n'existe pas.
     */
    public void setCurrentDirectory(String path) {
        if (!this.directories.containsKey(path)) {
            throw new IllegalArgumentException("Répertoire introuvable: " + path);
        }
        this.currentDirectory = path;
    }

    /**
     * Crée un nouveau répertoire dans le répertoire courant.
     * @param dirName Nom du répertoire à créer.
     * @return true si le répertoire a été créé, false s'il existe déjà.
     * @throws IllegalArgumentException Si dirName est vide ou null.
     */
    public boolean mkdir(String dirName) {
        if (dirName == null || dirName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom de répertoire invalide.");
        }

        String fullPath = getFullPath(dirName);

        if (this.directories.containsKey(fullPath)) {
            return false;
        }

        this.directories.put(fullPath, new HashSet<>());
        this.directories.get(this.currentDirectory).add(dirName);
        return true;
    }

    /**
     * Supprime un fichier ou un répertoire (doit être vide).
     * @param name Nom du fichier ou répertoire à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean rm(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String fullPath = getFullPath(name);

        if (this.files.containsKey(fullPath)) {
            this.files.remove(fullPath);
            this.directories.get(this.currentDirectory).remove(name);
            return true;
        } else if (this.directories.containsKey(fullPath)) {
            Set<String> contents = this.directories.get(fullPath);
            if (contents.isEmpty()) {
                this.directories.remove(fullPath);
                this.directories.get(this.currentDirectory).remove(name);
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Supprime un répertoire et tout son contenu (récursif).
     * @param name Nom du répertoire à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean rmdir(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String fullPath = getFullPath(name);

        if (!this.directories.containsKey(fullPath)) {
            return false;
        }

        Set<String> contents = new HashSet<>(this.directories.get(fullPath));
        for (String item : contents) {
            String itemPath = fullPath.equals(ROOT) ? ROOT + item : fullPath + "/" + item;
            if (this.directories.containsKey(itemPath)) {
                rmdir(item);
            } else if (this.files.containsKey(itemPath)) {
                this.files.remove(itemPath);
            }
        }

        this.directories.remove(fullPath);
        this.directories.get(this.currentDirectory).remove(name);
        return true;
    }

    /**
     * Liste le contenu du répertoire courant.
     * @return Liste des noms de fichiers et répertoires (les répertoires se terminent par "/").
     */
    public List<String> ls() {
        List<String> contents = new ArrayList<>();
        Set<String> dirContents = this.directories.getOrDefault(this.currentDirectory, new HashSet<>());

        for (String item : dirContents) {
            String fullPath = getFullPath(item);
            if (this.directories.containsKey(fullPath)) {
                contents.add(item + "/");
            } else if (this.files.containsKey(fullPath)) {
                contents.add(item);
            }
        }

        Collections.sort(contents);
        return contents;
    }

    /**
     * Liste le contenu d'un répertoire spécifique.
     * @param dirPath Chemin du répertoire à lister.
     * @return Liste des noms de fichiers et répertoires.
     * @throws IllegalArgumentException Si le répertoire n'existe pas.
     */
    public List<String> ls(String dirPath) {
        if (!this.directories.containsKey(dirPath)) {
            throw new IllegalArgumentException("Répertoire introuvable: " + dirPath);
        }

        List<String> contents = new ArrayList<>();
        Set<String> dirContents = this.directories.get(dirPath);

        for (String item : dirContents) {
            String fullPath = dirPath.equals(ROOT) ? ROOT + item : dirPath + "/" + item;
            if (this.directories.containsKey(fullPath)) {
                contents.add(item + "/");
            } else if (this.files.containsKey(fullPath)) {
                contents.add(item);
            }
        }

        Collections.sort(contents);
        return contents;
    }

    /**
     * Crée un nouveau fichier vide.
     * @param fileName Nom du fichier à créer.
     * @return true si le fichier a été créé, false s'il existe déjà.
     * @throws IllegalArgumentException Si fileName est vide ou null.
     */
    public boolean touch(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }

        String fullPath = getFullPath(fileName);

        if (this.files.containsKey(fullPath)) {
            return false;
        }

        this.files.put(fullPath, "");
        return true;
    }

    /**
     * Lit le contenu d'un fichier.
     * @param fileName Nom du fichier à lire.
     * @return Contenu du fichier, ou un message d'erreur si le fichier n'existe pas.
     */
    public String cat(String fileName) {
        String fullPath = getFullPath(fileName);
        return this.files.getOrDefault(fullPath, "Fichier introuvable: " + fileName);
    }

    /**
     * Écrit du contenu dans un fichier (écrasement).
     * @param fileName Nom du fichier.
     * @param content Contenu à écrire.
     * @return true si l'écriture a réussi, false sinon.
     */
    public boolean echo(String fileName, String content) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        String fullPath = getFullPath(fileName);
        this.files.put(fullPath, content);
        return true;
    }

    /**
     * Ajoute du contenu à la fin d'un fichier.
     * @param fileName Nom du fichier.
     * @param content Contenu à ajouter.
     * @return true si l'ajout a réussi, false sinon.
     */
    public boolean append(String fileName, String content) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        String fullPath = getFullPath(fileName);
        String existingContent = this.files.getOrDefault(fullPath, "");
        this.files.put(fullPath, existingContent + content);
        return true;
    }

    /**
     * Construit le chemin absolu à partir d'un nom de fichier/répertoire.
     * @param name Nom du fichier ou répertoire.
     * @return Chemin absolu.
     */
    private String getFullPath(String name) {
        if (this.currentDirectory.equals(ROOT)) {
            return ROOT + name;
        }
        return this.currentDirectory + "/" + name;
    }

    /**
     * Vérifie si un fichier ou répertoire existe.
     * @param name Nom du fichier ou répertoire.
     * @return true s'il existe, false sinon.
     */
    public boolean exists(String name) {
        String fullPath = getFullPath(name);
        return this.files.containsKey(fullPath) || this.directories.containsKey(fullPath);
    }

    /**
     * Retourne le chemin absolu du répertoire parent.
     * @param path Chemin dont on veut le parent.
     * @return Chemin absolu du répertoire parent.
     */
    public String getParentDirectory(String path) {
        if (path.equals(ROOT)) {
            return ROOT;
        }

        String[] parts = path.split("/");
        if (parts.length <= 2) {
            return ROOT;
        }

        StringBuilder parentPath = new StringBuilder();
        for (int i = 1; i < parts.length - 1; i++) {
            parentPath.append("/").append(parts[i]);
        }

        return parentPath.length() == 0 ? ROOT : parentPath.toString();
    }

    /**
     * Résout un chemin (relatif ou absolu) en chemin absolu.
     * @param path Chemin à résoudre (peut être relatif ou absolu).
     * @return Chemin absolu résolu.
     */
    public String resolvePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return this.currentDirectory;
        }

        path = path.trim();

        if (path.startsWith(ROOT)) {
            return normalizePath(path);
        }

        String resolvedPath = this.currentDirectory + "/" + path;
        return normalizePath(resolvedPath);
    }

    /**
     * Normalise un chemin (supprime les "/./" et résout les "/../").
     * @param path Chemin à normaliser.
     * @return Chemin normalisé.
     */
    public String normalizePath(String path) {
        if (path.equals(ROOT)) {
            return ROOT;
        }

        String[] parts = path.split("/");
        List<String> normalizedParts = new ArrayList<>();

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            } else if (part.equals("..")) {
                if (!normalizedParts.isEmpty()) {
                    normalizedParts.remove(normalizedParts.size() - 1);
                }
            } else {
                normalizedParts.add(part);
            }
        }

        if (normalizedParts.isEmpty()) {
            return ROOT;
        }

        StringBuilder normalizedPath = new StringBuilder();
        for (String part : normalizedParts) {
            normalizedPath.append("/").append(part);
        }

        return normalizedPath.toString();
    }

    /**
     * Vérifie si un chemin est un répertoire.
     * @param path Chemin à vérifier.
     * @return true si c'est un répertoire, false sinon.
     */
    public boolean isDirectory(String path) {
        return this.directories.containsKey(path);
    }

    /**
     * Vérifie si un chemin est un fichier.
     * @param path Chemin à vérifier.
     * @return true si c'est un fichier, false sinon.
     */
    public boolean isFile(String path) {
        return this.files.containsKey(path);
    }
}

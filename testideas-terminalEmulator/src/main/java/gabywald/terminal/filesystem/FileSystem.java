package gabywald.terminal.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe représentant un système de fichiers simplifié pour l'émulateur de terminal.
 * Gère les fichiers et répertoires sous forme de structure arborescente.
 *
 * <p>
 * <b>Fonctionnalités principales :</b>
 * <ul>
 *   <li>Création et suppression de fichiers et répertoires.</li>
 *   <li>Navigation entre répertoires (cd, pwd).</li>
 *   <li>Lecture et écriture de fichiers (cat, echo).</li>
 *   <li>Vérification de l'existence de fichiers/répertoires.</li>
 *   <li>Gestion des chemins absolus et relatifs.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>
 * FileSystem fs = new FileSystem();
 * fs.mkdir("test");          // Crée un répertoire "test"
 * fs.touch("file.txt");      // Crée un fichier "file.txt"
 * fs.echo("file.txt", "Hello"); // Écrit "Hello" dans "file.txt"
 * String content = fs.cat("file.txt"); // Lit le contenu
 * </pre>
 * </p>
 */
public class FileSystem {
    // Répertoire racine (constante)
    private static final String ROOT = "/";

    // Répertoire courant
    private String currentDirectory;

    // Structure des répertoires : clé = chemin absolu, valeur = ensemble des noms de fichiers/répertoires
    private Map<String, Set<String>> directories;

    // Structure des fichiers : clé = chemin absolu, valeur = contenu du fichier
    private Map<String, String> files;

    /**
     * Constructeur : initialise le système de fichiers avec un répertoire racine.
     * Le répertoire racine est créé automatiquement.
     */
    public FileSystem() {
        this.currentDirectory	= FileSystem.ROOT;
        this.directories		= new HashMap<>();
        this.files				= new HashMap<>();

        // Initialiser le répertoire racine
        this.directories.put(ROOT, new HashSet<>());
    }

    // ============================================
    // Méthodes de base pour le système de fichiers
    // ============================================

    /**
     * Retourne le chemin du répertoire racine.
     * @return Chemin absolu du répertoire racine ("/").
     */
    public String getRoot() { return FileSystem.ROOT; }

    /**
     * Retourne le répertoire courant.
     * @return Chemin absolu du répertoire courant.
     */
    public String getCurrentDirectory() { return this.currentDirectory; }

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

    // ============================================
    // Méthodes pour les répertoires
    // ============================================

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

        // Vérifier si le répertoire existe déjà
        if (this.directories.containsKey(fullPath)) {
            return false;
        }

        // Créer le répertoire
        this.directories.put(fullPath, new HashSet<>());

        // Ajouter le répertoire au répertoire parent
        this.directories.get(this.currentDirectory).add(dirName);

        return true;
    }

    /**
     * Supprime un répertoire ou un fichier.
     * @param name Nom du répertoire ou fichier à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean rm(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String fullPath = getFullPath(name);

        // Supprimer un fichier
        if (this.files.containsKey(fullPath)) {
            this.files.remove(fullPath);
            this.directories.get(this.currentDirectory).remove(name);
            return true;
        }
        // Supprimer un répertoire (doit être vide)
        else if (this.directories.containsKey(fullPath)) {
            Set<String> contents = this.directories.get(fullPath);
            if (contents.isEmpty()) {
                this.directories.remove(fullPath);
                this.directories.get(this.currentDirectory).remove(name);
                return true;
            } else {
                return false; // Répertoire non vide
            }
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

        // Supprimer récursivement le contenu
        Set<String> contents = new HashSet<>(this.directories.get(fullPath));
        for (String item : contents) {
            String itemPath = fullPath.equals(ROOT) ? ROOT + item : fullPath + "/" + item;
            if (this.directories.containsKey(itemPath)) {
                rmdir(item); // Supprimer récursivement
            } else if (this.files.containsKey(itemPath)) {
                this.files.remove(itemPath);
            }
        }

        // Supprimer le répertoire lui-même
        this.directories.remove(fullPath);
        this.directories.get(this.currentDirectory).remove(name);

        return true;
    }

    /**
     * Liste le contenu du répertoire courant.
     * @return Liste des noms de fichiers et répertoires (les répertoires se terminent par "/").
     */
    public List<String> ls() {
        List<String> contents = new ArrayList<String>();
        Set<String> dirContents = this.directories.getOrDefault(this.currentDirectory, new HashSet<>());

        for (String item : dirContents) {
            String fullPath = getFullPath(item);
            if (this.directories.containsKey(fullPath)) {
                contents.add(item + "/"); // Ajouter "/" pour les répertoires
            } else if (this.files.containsKey(fullPath)) {
                contents.add(item);
            }
        }

        Collections.sort(contents); // Trier par ordre alphabétique
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

    // ============================================
    // Méthodes pour les fichiers
    // ============================================

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
            return false; // Fichier déjà existant
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

    // ============================================
    // Méthodes utilitaires pour les chemins
    // ============================================

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
     * @throws IllegalArgumentException Si le chemin est invalide.
     */
    public String resolvePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return this.currentDirectory;
        }

        path = path.trim();

        // Si le chemin est absolu (commence par "/")
        if (path.startsWith(ROOT)) {
            return normalizePath(path);
        }

        // Sinon, c'est un chemin relatif : le résoudre par rapport au répertoire courant
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
                continue; // Ignorer les parties vides ou "."
            } else if (part.equals("..")) {
                if (!normalizedParts.isEmpty()) {
                    normalizedParts.remove(normalizedParts.size() - 1); // Remonter d'un niveau
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

    // ============================================
    // Méthodes pour la gestion des permissions (optionnel)
    // ============================================

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

    // ============================================
    // Méthodes pour la sérialisation (optionnel)
    // ============================================

    /**
     * Exporte l'état du système de fichiers sous forme de chaîne de caractères.
     * Utile pour sauvegarder/restaurer l'état.
     * @return Représentation textuelle du système de fichiers.
     */
    public String exportState() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Directories ===\\n");
        for (Map.Entry<String, Set<String>> entry : this.directories.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\\n");
        }
        sb.append("\\n=== Files ===\\n");
        for (Map.Entry<String, String> entry : this.files.entrySet()) {
            sb.append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\\n");
        }
        return sb.toString();
    }

    /**
     * Importe l'état du système de fichiers à partir d'une chaîne de caractères.
     * @param state Représentation textuelle du système de fichiers.
     */
    public void importState(String state) {
        // TODO: Implémenter la désérialisation
        throw new UnsupportedOperationException("Non implémenté.");
    }
}

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static final StringBuilder LOG = new StringBuilder();

    public static void main(String[] args) {
        makeDir("src");
        makeDir("res");
        makeDir("savegames");
        makeDir("temp");
        makeDir("src/main");
        makeDir("src/test");
        makeFile("src/main", "Main.java");
        makeFile("src/main", "Utils.java");
        makeDir("res/drawables");
        makeDir("res/vectors");
        makeDir("res/icons");
        makeFile("temp", "temp.txt");

        GameProgress gp1 = new GameProgress(30, 5, 6, 205);
        GameProgress gp2 = new GameProgress(70, 15, 20, 511.5);
        GameProgress gp3 = new GameProgress(58, 9, 25, 1015);

        saveGames("C:/Games/savegames/save1.dat", gp1);
        saveGames("C:/Games/savegames/save2.dat", gp2);
        saveGames("C:/Games/savegames/save3.dat", gp3);

        List<String> listGames = new ArrayList<>();
        listGames.add("C:/Games/savegames/save1.dat");
        listGames.add("C:/Games/savegames/save2.dat");
        listGames.add("C:/Games/savegames/save3.dat");

        zipFiles("C:/Games/savegames/zip.zip", listGames);

        deleteFile(listGames);

        openZip("C:/Games/savegames/zip.zip", "C:/Games/savegames/");

        System.out.println(openProgress("C:/Games/savegames/save1.dat"));
        System.out.println(openProgress("C:/Games/savegames/save2.dat"));
        System.out.println(openProgress("C:/Games/savegames/save3.dat"));

        log("C:/Games/temp/temp.txt", LOG);

    }

    // Задача 1
    public static void log(String fileName, StringBuilder sb) {
        try (FileWriter fr = new FileWriter(fileName, true)) {
            fr.write(sb.toString());
            fr.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void makeDir(String name) {
        File dir = new File("C:/Games/" + name);
        if (dir.mkdir()) {
            LOG.append(LocalDateTime.now() + " Папка " + name + " создана\n");
        } else if (dir.exists()) {
            LOG.append(LocalDateTime.now() + " Папка " + name + " уже существует\n");
        } else {
            LOG.append(LocalDateTime.now() + " Папка " + name + " НЕ создана, ошибка\n");
        }
    }

    public static void makeFile(String nameDir, String nameFile) {
        File file = new File("C:/Games/" + nameDir, nameFile);
        try {
            if (file.createNewFile()) {
                LOG.append(LocalDateTime.now() + " Файл " + nameFile + " создан\n");
            } else if (file.exists()) {
                LOG.append(LocalDateTime.now() + " Файл " + nameFile + " уже существует\n");
            } else {
                LOG.append(LocalDateTime.now() + " Файл " + nameFile + " НЕ создан, ошибка\n");
            }
        } catch (IOException e) {
            LOG.append(LocalDateTime.now() + " Папка " + nameDir + " не найдена, файл " + nameFile + " НЕ создан\n");
            throw new RuntimeException(e);
        }
    }

    // Задача 2
    public static void saveGames(String fullWay, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(fullWay);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            LOG.append(LocalDateTime.now() + " СОХРАНЕНИЕ: файл " + fullWay + " сохранен\n");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            LOG.append(LocalDateTime.now() + " ОШИБКА СОХРАНЕНИЯ: файл " + fullWay + " НЕ сохранен\n");
        }
    }

    public static void zipFiles(String fullWayToZip, List<String> fullWayToFile) {
        int i = 1;
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fullWayToZip))) {
            for (String a : fullWayToFile) {
                try (FileInputStream fis = new FileInputStream(a)) {
                    ZipEntry entry = new ZipEntry("save" + i + ".dat");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                    LOG.append(LocalDateTime.now() + " АРХИВАЦИЯ: файл " + a + " добавлен в архив\n");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    LOG.append(LocalDateTime.now() + " ОШИБКА АРХИВАЦИИ: файл " + a + " НЕ добавлен в архив\n");
                }
                i++;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteFile(List<String> nameList) {
        for (String name : nameList) {
            File file = new File(name);
            if (file.delete()) {
                LOG.append(LocalDateTime.now() + " файл " + name + " УДАЛЕН\n");
            } else {
                LOG.append(LocalDateTime.now() + " файл " + name + " НЕ удален\n");
                ;
            }
        }
    }

    // Задача 3
    public static void openZip(String wayToFile, String dirToOpen) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(wayToFile))) {
            ZipEntry entry;
            //String name;
            while ((entry = zin.getNextEntry()) != null) {
                //name = entry.getName();
                FileOutputStream fout = new FileOutputStream(dirToOpen + entry.getName()); //name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                LOG.append(LocalDateTime.now() + " файл " + entry.getName() + " распакован\n");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            LOG.append(LocalDateTime.now() + " ОШИБКА распаковки архива: файл " + wayToFile + " НЕ распакован\n");
        }
    }

    public static GameProgress openProgress(String fullWayToFile) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(fullWayToFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
            LOG.append(LocalDateTime.now() + " ДЕСЕРИАЛИЗАЦИЯ файла " + fullWayToFile + " выполнена\n");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            LOG.append(LocalDateTime.now() + " ОШИБКА: ДЕСЕРИАЛИЗАЦИЯ файла " + fullWayToFile + " НЕ выполнена\n");
        }
        return gameProgress;
    }
}
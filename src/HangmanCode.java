import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class HangmanCode {

    private static boolean testing = false;
    private static boolean start = true;
    private static int difficulty = 0;
    private static String wordToGuess = "";
    private static String hiddenWord = "";
    private static List<String> availableLettersList = new ArrayList<>();
    private static List<String> usedLettersList = new ArrayList<>();
    private static int lives = 6;
    private static String definition= "";

    public static void play() {
        if (start) {                            //ennek az if-nek a tartalma csak akkor fut le, ha kezdődik a játék (a start boolean értéke az if után false-ra állítódik)
            aFreshStart();                      //kiraktam egy külön metódusba, hogy könyebben olvasható legyen a kód
        }
        start = false;                          //így az előbbieket csak a játék első körében futtatja le
        System.out.println("Lives: " + lives);
        if (lives > 0) {                        //ha nincs több élet, végéhez ér a játék
            gameCode();                         //ez már a játék folyama
        } else {
            System.out.println("Game Over, the answer was this: " + wordToGuess);
            oneMoreGame();
        }
    }

    private static void syllabusChapterMapCreator(String filePath){
        HashMap<String, String> map = new HashMap<>();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length >= 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    map.put(key, value);
                } else {
                    System.out.println("ignoring line: " + line);
                }
            }
            reader.close();
        } catch (Exception exceptionSyllabusChapterMapCreator){
            System.out.println("exception in listFromFile(): " + exceptionSyllabusChapterMapCreator);
        }
        List<String> mapKeys = new ArrayList<>();
        for (String keys : map.keySet()){
            mapKeys.add(keys);
        }
        randomWordCreator(mapKeys);
        definition = map.get(wordToGuess);
    }

    private static List fileReader(String filePath) {
        String myFileContent = "";
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                myFileContent = myFileContent + myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException exceptionFromFileReader) {
            System.out.println("exception in listFromFile(): " + exceptionFromFileReader);
        }

        String[] strArray = myFileContent.split(",");
        List<String> listFromFileReader = new ArrayList<>();
        for (int i = 0; i < strArray.length; i++) {
            listFromFileReader.add(strArray[i].trim());
        }
        return listFromFileReader;
    }

    private static void syllabusChapterListCreator(String filePath) {
        List<String> syllabusChapterOneWordsList = new ArrayList<>(fileReader(filePath)); //legyártja a listát a fileReader() segítségével
        randomWordCreator(syllabusChapterOneWordsList);
    }

    private static void withOrWithoutDefinition(int pathNumber) {
        List<Integer> validNumbers = Arrays.asList(1, 2);
        System.out.println("**********************************************************************");
        System.out.println("*        Want to use definitions for keywords? Yes(1) or No(2)       *");
        System.out.println("**********************************************************************");
        int selector = askForInt(validNumbers);
        if (selector == 1) {
            syllabusChapterMapCreator("puzzlefiles/syllabusC" + pathNumber + "KeyWordsWithDefinitions.txt");
        } else {
            syllabusChapterListCreator("puzzlefiles/syllabusC" + pathNumber + "KeyWords.txt"); //a sima ","-vel ellátott szavas txt-t használja.
        }
    }

    private static void printMenuWordsToPlay() {
        System.out.println("**********************************************************************");
        System.out.println("*              What words would you like to play with?               *");
        System.out.println("*          (1) Syllabus keywords                                     *");
        System.out.println("*          (2) Countries and Capitals                                *");
        System.out.println("*          (0) Test mode on (you can see the answer)                 *");
        System.out.println("**********************************************************************");
    }

    private static void printMenuForSyllabusKeywords(){
        System.out.println("**********************************************************************");
        System.out.println("*         From which syllabus chapter do you want the puzzle?        *");
        System.out.println("*          (1) Syllabus Chapter1 keywords                            *");
        System.out.println("*          (2) Syllabus Chapter2 keywords                            *");
        System.out.println("*          (3) Syllabus Chapter3 keywords                            *");
        System.out.println("*          (4) Syllabus Chapter4 keywords                            *");
        System.out.println("*          (5) Syllabus Chapter5 keywords                            *");
        System.out.println("*          (7) Syllabus all Chapters keywords                        *");
        System.out.println("**********************************************************************");
    }

    private static void menuWordsToPlayWith() {
        List<Integer> validNumbers = Arrays.asList(0, 1, 2);
        printMenuWordsToPlay();
        int chosenNumber = askForInt(validNumbers); //ennek az int-nek a segítségével választja ki a listFromFile() metódus, hogy melyik oszlopból csináljon listát.
        switch (chosenNumber) {
            case 1:
                menuWordFromSyllabus();
                break;
            case 2:
                countriesOrCapitalsSelector();
                break;
            case 0:
                testMode();
                break;
            default:
                System.out.println("Error with the switch in wordsToPlayWith(), used the default branch!");
        }
    }

    private static void menuWordFromSyllabus(){
        List<Integer> validNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        printMenuForSyllabusKeywords();
        int chosenNumber = askForInt(validNumbers); //ennek az int-nek a segítségével választja ki a listFromFile() metódus, hogy melyik oszlopból csináljon listát.
        switch (chosenNumber) {
            case 1:
                withOrWithoutDefinition(1);
                break;
            case 2:
                withOrWithoutDefinition(2);
                break;
            case 3:
                withOrWithoutDefinition(3);
                break;
            case 4:
                withOrWithoutDefinition(4);
                break;
            case 5:
                withOrWithoutDefinition(5);
                break;
            case 6:
                withOrWithoutDefinition(6);
                break;
            case 7:
                withOrWithoutDefinition(7);
                break;
            default:
                System.out.println("Error with the switch in menuWordFromSyllabus(), used the default branch!");
        }
    }

    private static void gameCode() { //a játék folyamata
        if (hiddenWord.contains("_")) {          //így nézi meg, hogy ki lett-e találva az összes betü (win)
            if (usedLettersList.size() > 0) {    //ha már van használt betű, akkor kinyomtatja, hogy a játékos lássa, hogy azt már nem lehet választani.
                System.out.println("Used letters list: " + usedLettersList);
            }
            if (!definition.equals("")) { //ha nincs beállítva definició, nem veszi ezt figyelembe (syllabusnál lehet definiciót kérni)
                System.out.println("Definition: " + definition);
            }
            if (testing) { System.out.println("for test: " + wordToGuess); }
            System.out.println("Status of word to be guessed: " + hiddenWord);
            letterSearcher(askForLetter(availableLettersList)); //bekéri inputként a tippelt betüt, lekezeli a listákat, megnézi van-e a feladványban a betü, ha van beírja, ha nincs életet vesz le
            play();
        } else {
            youWon();
        }
    }

    private static void aFreshStart() { //csak ha új játék kezdődik, kezdeti beállításokért felelős
        reset();                            //ha új játékot szeretnénk kezdeni, kitörli az előző játék adatait
        setDifficulty();                    //ez a metódus inputból bekéri a usertől hogy milyen nehéz legyen a játék
        menuWordsToPlayWith();                  //kilehet választani listából, milyen szavakkal szeretnénk játszani
        hiddenWordMaker();                  //ez készíti el a kitalálandó szó rejtett változatát
        lettersListMaker();                 //elkészíti az eredeti, és a használandó betüs listákat
    }

    private static void testMode() {
        System.out.println("Want to play the game in test mode? If yes, type \"yes\" or just \"y\"!");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.next();
        if (answer.equals("yes") || answer.equals("y")) {
            testing = true;
            System.out.println("The game is in test mode!");
        }
        menuWordsToPlayWith();
    }

    private static void youWon() {
        System.out.println("You won!");
        oneMoreGame();
    }

    private static void oneMoreGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("One more game? If yes, type \"yes\" or just \"y\"!");
        String answer = scanner.next();
        if (answer.equals("yes") || answer.equals("y")) {
            start = true;
            play();
        } else exit();
    }

    public static void exit() {
        System.out.println("Good bye!");
        System.out.println();
        System.out.println("Author: David Diriczi");
        System.out.println("Has collected the Syllabus definitions: Istvan Gyurika");
        System.exit(1);
    }

    private static void letterSearcher(String inputLetter) {
        availableLettersList.remove(inputLetter); //kiveszi az input betüt az elérhetőbetük listából
        usedLettersList.add(inputLetter); //berakja az input betüt a használtbetük listába
        String lowerCaseWordToGuess = wordToGuess.toLowerCase(); //azért van a lowerCase, hogy a nagy kezdőbetüket is átvizsgálja kisbetüs inputra
        if (lowerCaseWordToGuess.contains(inputLetter)) {
            String[] stringArrayFromLCWordToGuess = lowerCaseWordToGuess.split(""); //a lowerCasewordToGuess Stringet szétdarabolja betükre és betölti a tömbbe.
            String[] stringArrayFromWordToGuess = wordToGuess.split(""); //ezzel tölti fel a cloneHiddenWord-öt, azért kell ez, hogy ha van nagy betü, akkor azt is rakja bele.
            String[] stringArrayFromHiddenWord = hiddenWord.split(""); //ez alapján néz meg, hogy van-e már kitalált betü.
            List<Integer> indexesOfMatchingLetters = new ArrayList<>();
            List<Integer> indexesOfNonMatchingLetters = new ArrayList<>();
            for (int i = 0; i < stringArrayFromLCWordToGuess.length; i++) { //ha a szóban az input stringhez, szóközhöz vagy kötöjelhez ér, akkor azt hozzáadja a hiddenWord Stringhez, egyébként alsó vonalat.
                if (stringArrayFromLCWordToGuess[i].equals(inputLetter) || stringArrayFromLCWordToGuess[i].equals(" ") || stringArrayFromLCWordToGuess[i].equals("-")) { //ha benne van a betü, vagy a szóköz vagy a kötőjel
                    indexesOfMatchingLetters.add(i); //elrakja az integer listába az index értékét
                } else if (!stringArrayFromHiddenWord[i].equals("_")) { //ha már van benne kitalált betű
                    indexesOfMatchingLetters.add(i);
                } else {
                    indexesOfNonMatchingLetters.add(i); //Ezek már csak a "_" lehetnek!
                }
            }
            String cloneHiddenWord = "";
            for (int j = 0; j < wordToGuess.length(); j++) {
                if (indexesOfMatchingLetters.contains(j)) {
                    cloneHiddenWord += stringArrayFromWordToGuess[j];
                } else if (indexesOfNonMatchingLetters.contains(j)) {
                    cloneHiddenWord += "_";
                }
            }
            hiddenWord = cloneHiddenWord;
        } else {
            lives -= difficulty;
        }
    }

    private static String askForLetter(List<String> availableLettersList) { //csak az availableLettersList stringjeit lehet beírni inputnak
        while (true) { //addig fut a loop, amíg nem fut hibába (vagyis amíg nem azt írják be, amit szeretnénk)
            try {
                Scanner reader = new Scanner(System.in);
                System.out.print("Please provide an letter (or quit) " + availableLettersList + ":");
                System.out.println();
                String inputLetter = reader.nextLine().toLowerCase();
                if (inputLetter.equals(wordToGuess) || inputLetter.equals(wordToGuess.toLowerCase())) {
                    youWon();
                }
                if (availableLettersList.contains(inputLetter)) { //ez az if megnézi, a validOperators listában megtalálható-e az inputban megadott érték.
                    if (inputLetter.equals("quit")) {
                        exit();
                    } else {
                        return inputLetter;
                    }
                } else {
                    System.out.println("Invalid user input!");
                }
            } catch (InputMismatchException exceptionAskForLetter) {
                System.out.println("exception in askForLetter: " + exceptionAskForLetter); //ez nem tudom mikor tudna felugrani...
            }
        }
    }

    private static void lettersListMaker() { //létrehozza a választható betük listáját.
        for (char c = 'a'; c <= 'z'; c++) {
            availableLettersList.add(String.valueOf(c));
        }
        availableLettersList.add("quit");
    }

    private static void hiddenWordMaker() {
        String[] stringArrayFromWordToGuess = wordToGuess.split(""); //a wordToGuess Stringet szétdarabolja betükre és betölti a tömbbe.
        for (String elementOfStringArrayFromWordToGuess : stringArrayFromWordToGuess) { //ha a szóban szóközhöz vagy kötöjelhez ér, akkor azt hozzáadja a hiddenWord Stringhez, egyébként alsó vonalat.
            if (elementOfStringArrayFromWordToGuess.equals(" ") || elementOfStringArrayFromWordToGuess.equals("-")) {
                hiddenWord += elementOfStringArrayFromWordToGuess;
            } else {
                hiddenWord += "_";
            }
        }
    }

    public static void randomWordCreator(List<String> wordsFromFile) {
        Collections.shuffle(wordsFromFile);
        wordToGuess = wordsFromFile.get(0).trim(); //az összekevert listának kiveszi a 0. elemét és leveszi róla a fehér karaktereket
    }

    public static void listFromFile(int chosenNumber, String path) {  //ez rakja össze a fileból a listát, ha a txt file formája ilyen: első szó | második szó
        List<String> wordsFromFile = new LinkedList<>();
        String line; //ebbe a Stringbe fogja másolni a fileból kiolvasott Stringet, amit aztán majd átmásol a parts tömbbe
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(path));
            while ((line = fileReader.readLine()) != null) { //addig megy, amíg a line értéke null lesz (mindent kiolvasott a fileból)
                String[] parts = line.split("\\|", 2); //escape-elni kellett a "|" karaktert, mert az a regex beépített karaktere
                if (parts.length >= 2) { //kihagyja a file azon sorait, amiben nincs minimum 2 szó
                    wordsFromFile.add(parts[chosenNumber - 1]); //a metódus fogadott paramétere alapján az ország vagy a főváros oszlopot rakja bele a listába
                } else {
                    System.out.println("ignoring line: " + line);
                }
            }
            fileReader.close();
        } catch (Exception exceptionListFromFile) {
            System.out.println("exception in listFromFile(): " + exceptionListFromFile);
        }

        randomWordCreator(wordsFromFile);
    }

    private static void countriesOrCapitalsSelector() {
        List<Integer> countriesOrCapitalsSelector = Arrays.asList(1, 2);
        System.out.println("**********************************************************************");
        System.out.println("*        Please select play mode: Countries(1) or Capitals(2)        *");
        System.out.println("**********************************************************************");
        listFromFile(askForInt(countriesOrCapitalsSelector), "puzzlefiles/countries-and-capitals.txt"); //az int alapján fogja eldönteni a listFromFile, hogy melyik oszlopot hazsnálja
    }

    private static void setDifficulty() {
        List<Integer> listOfDifficulty = Arrays.asList(1, 2, 3);
        System.out.println("**********************************************************************");
        System.out.println("*     Please choose a difficulty: easy(1), medium(2), hard(3)!       *");
        System.out.println("**********************************************************************");
        difficulty = askForInt(listOfDifficulty);
    }

    private static int askForInt(List<Integer> validNumbers) { //a paraméterben lévő lista tartalma adja meg, hogy mi a valid Integer tartomány.
        while (true) { //addig fut a loop, amíg nem fut hibába (vagyis amíg nem azt írják be, amit szeretnénk)
            try {
                Scanner reader = new Scanner(System.in);
                System.out.print("Please provide an number " + validNumbers + ":");
                System.out.println();
                int inputInteger = reader.nextInt();
                if (validNumbers.contains(inputInteger)) { //ez az if megnézi, a validOperators listában megtalálható-e az inputban megadott érték.
                    return inputInteger;
                } else {
                    System.out.println("Invalid number!");
                }
            } catch (InputMismatchException exceptionAskForInt) {
                System.out.println("exception in askForInt(): " + exceptionAskForInt); //ez nem tudom mikor tudna felugrani...
            }
        }
    }

    private static void reset() { //ha új játékot szeretnénk kezdeni, kitörli az előző játék adatait
        testing = false;
        difficulty = 0;
        wordToGuess = "";
        hiddenWord = "";
        availableLettersList.clear();
        usedLettersList.clear();
    }
}
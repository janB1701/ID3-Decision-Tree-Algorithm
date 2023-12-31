import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.lang.Math;

public class Id3 {
    static String pathFinder = null;
    static List<Node> tree = new LinkedList<>();
    static ArrayList<String> dataFromFile = new ArrayList<>();
    static ArrayList<String> dataFromTestFile = new ArrayList<>();
    static Map<String, Integer> attributes = new HashMap<>();
    static int noOfAtributes = 0;
    static ArrayList<Integer> usedAttributes = new ArrayList<>();
    static int border = -1;
    static List<String> branches = new LinkedList<>();
    static List<String> dobiveneOznake = new LinkedList<>();
    static List<String> stvarneOznake = new LinkedList<>();

    static class Node{
        private String state;
        private Node parent;
        private String atribut;
        private int noIndexAppear;
        private Node childNode;
        private Set<Node> children;

        public Node(String atribut, String state, Node parent, int maxIndex, Node child, Set<Node> children) {
            this.state = state;
            this.parent = parent;
            this.atribut = atribut;
            this.noIndexAppear = maxIndex;
            this.childNode = child;
        }


        public void setChildNode(Node childNode) {
            this.childNode = childNode;
        }

        public Node getChildNode() {
            return childNode;
        }

        public int getNoIndexAppear() {
            return noIndexAppear;
        }

        public String getState() {
            return state;
        }

        public Set<Node> getChildren() {
            return children;
        }

        public void setChildren(Set<Node> children) {
            this.children = children;
        }

        public Node getParent() {
            return parent;
        }

        public String getAtribut() {
            return atribut;
        }

        public void setAtribut(String atribut) {
            this.atribut = atribut;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public int getDepth () {
            int depth = 0;
            Node current = this.getParent();
            while (current != null) {
                depth++;
                current = current.getParent();
            }
            return depth;
        }

        public static void getFinalPath (Node finalNode) { //get the path for the final node
            int cnt = 0;
            Stack ispis = new Stack();
            Node current = finalNode;
            while (current.getParent() != null) {
                ispis.push (current.getState());
                current = current.getParent();
            }
            while (!ispis.empty()) {
                if (cnt == 0) {
                    pathFinder += " => " + ispis.pop();
                    cnt++;
                }else {
                    pathFinder += " => " + ispis.pop();
                }
            }
        }
    }

    public static void main(String[] args) {
        readCSV (args[0], args[1]);
        noOfAtributes = dataFromFile.get(0).split(",").length;
        if (args.length == 3) border =Integer.parseInt (args[2]);
        id3();
        for (Node node : tree) {
            System.out.println(node.getState()+ " " + node.getDepth());
        }
        System.out.println("[BRANCHES]:");
        branches ();
        System.out.print("[PREDICTIONS]: ");
        predict();
        matrica();
    }

    private static void predict() {
        Map<String, String> mapLine;
        String[] lineArr = null;
        int max = 0;
        boolean find = false;
        String veciKlas = null;
        int accurate = 0;

        for (int i = 1; i<dataFromTestFile.size(); i++) {
            mapLine = new HashMap<>();
            lineArr = dataFromTestFile.get(i).split(",");
            for (int j = 0; j<lineArr.length; j++) {
                mapLine.put(dataFromTestFile.get(0).split(",")[j], lineArr[j]);
            }
            Node root = tree.get(0);
            while (root.getChildren() != null) {
                for (Node children : root.getChildren()) {
                    if (children.getState().equals(mapLine.get(root.getAtribut()))) {
                        root = children;
                        find = true;
                        break;
                    }
                }
                //ako je dosao neki unseen value
                if (!find) {
                    Map<String, Integer> ponavljanja = new HashMap<>();
                    String[] branchArr = null;
                    for (String branch : branches) {
                        branchArr = branch.split(" ");
                        if (branch.contains(root.getAtribut())) {
                            if (ponavljanja.keySet().contains(branchArr[branchArr.length-1])) {
                                int current = ponavljanja.get(branchArr[branchArr.length-1]) + 1;
                                ponavljanja.put(branchArr[branchArr.length-1], current);
                            }else {
                                ponavljanja.put(branchArr[branchArr.length-1], 1);
                            }
                        }
                    }
                    int ukupno = 0;
                    for (String key : ponavljanja.keySet()) {
                        ukupno += ponavljanja.get(key);
                    }
                    boolean isto = false;
                    String maxKey = null;
                    for (String key : ponavljanja.keySet()) {
                        double dijeli = (double) ukupno/ponavljanja.size();
                        if (dijeli == ponavljanja.get(key) && (ponavljanja.size() > 1)) {
                            isto = true;
                        }
                        maxKey = key;
                        break;
                    }
                    veciKlas = null;
                    if (!isto){
                        for (String key : ponavljanja.keySet()) {
                            if (ponavljanja.get(key) > max) {
                                max = ponavljanja.get(key);
                                veciKlas = key;

                            }
                        }
                        System.out.print(veciKlas + " ");
                        String line = dataFromTestFile.get(i).split(",")[noOfAtributes-1];
                        dobiveneOznake.add(veciKlas);
                        stvarneOznake.add(line);
                        if (veciKlas.equals(line))
                            accurate++;
                    }
                    else {
                        for (String key : ponavljanja.keySet()) {
                            if (key.compareTo(maxKey) < 0) {
                                maxKey = key;

                            }
                        }
                        System.out.print(maxKey + " ");
                        String line = dataFromTestFile.get(i).split(",")[noOfAtributes-1];
                        dobiveneOznake.add(maxKey);
                        stvarneOznake.add(line);
                        if (maxKey.equals(line))
                            accurate++;
                    }
                    break;
                }
                if (root.getChildren() == null) {
                    System.out.print(root.getAtribut() + " ");
                    dobiveneOznake.add (root.getAtribut());
                    String line = dataFromTestFile.get(i).split(",")[noOfAtributes-1];
                    stvarneOznake.add (line);
                    String rootAtr = root.getAtribut();
                    if (rootAtr.equals(line))
                        accurate++;
                }
                find = false;
            }
        }
        System.out.println();
        DecimalFormat df = new DecimalFormat("###.#####");
        //System.out.print("[ACCURACY]: " + df.format((double)accurate/(dataFromTestFile.size()-1)).toString().replace(",", "."));
        System.out.printf("[ACCURACY]: %s", String.format("%.5f", (double)accurate/(dataFromTestFile.size()-1)).replace(",", "."));
        System.out.println();
        //System.out.printf("[ACCURACY]: %s", String.format((double)accurate/(dataFromTestFile.size()-1), "%.5f"));
    }

    private static void matrica () {
        int m11 = 0, m12 = 0,m13 = 0,m21 = 0,m22 = 0,m23 = 0,m31 = 0,m32 = 0,m33 = 0;
        Set<String> oznake = new HashSet<>();

        for (String ozn : stvarneOznake) oznake.add(ozn);
        ArrayList<String> oznakeSorted = new ArrayList<>(oznake);
        Collections.sort(oznakeSorted);
        if (oznake.size() == 2) {
            for (int i = 0; i < stvarneOznake.size(); i++) {
                if (stvarneOznake.get(i).equals(oznakeSorted.get(0)) && dobiveneOznake.get(i).equals(oznakeSorted.get(0)))
                    m11 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(0)) && dobiveneOznake.get(i).equals(oznakeSorted.get(1)))
                    m12 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(1)) && dobiveneOznake.get(i).equals(oznakeSorted.get(0)))
                    m21 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(1)) && dobiveneOznake.get(i).equals(oznakeSorted.get(1)))
                    m22 += 1;
            }
            System.out.println("[CONFUSION_MATRIX]:");
            System.out.println(m11 + " " + m12);
            System.out.println(m21 + " " + m22);
        }else if (oznake.size() == 3) {
            for (int i = 0; i < stvarneOznake.size(); i++) {
                if (stvarneOznake.get(i).equals(oznakeSorted.get(0)) && dobiveneOznake.get(i).equals(oznakeSorted.get(0)))
                    m11 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(0)) && dobiveneOznake.get(i).equals(oznakeSorted.get(1)))
                    m12 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(0)) && dobiveneOznake.get(i).equals(oznakeSorted.get(2)))
                    m13 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(1)) && dobiveneOznake.get(i).equals(oznakeSorted.get(0)))
                    m21 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(1)) && dobiveneOznake.get(i).equals(oznakeSorted.get(1)))
                    m22 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(1)) && dobiveneOznake.get(i).equals(oznakeSorted.get(2)))
                    m23 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(2)) && dobiveneOznake.get(i).equals(oznakeSorted.get(0)))
                    m31 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(2)) && dobiveneOznake.get(i).equals(oznakeSorted.get(1)))
                    m32 += 1;
                if (stvarneOznake.get(i).equals(oznakeSorted.get(2)) && dobiveneOznake.get(i).equals(oznakeSorted.get(2)))
                    m33 += 1;
            }
            System.out.println("[CONFUSION_MATRIX]:");
            System.out.println(m11 + " " + m12 + " " + m13);
            System.out.println(m21 + " " + m22 + " " + m23);
            System.out.println(m31 + " " + m32 + " " + m33);
        }
    }

    private static void branches() {
        List<Node> nodesToWriteDown = new LinkedList<>();
        Node currentNode = null;
        Node writeDown = null;
        int toggle = 0;
        int depth = 0;
        String branch = "";
        for (Node node : tree) {
            nodesToWriteDown = new LinkedList<>();
            branch = "";
            if (node.getChildNode() == null) {
                while (node.getParent() != null) {
                    nodesToWriteDown.add(node);
                    node = node.getParent();
                }
                nodesToWriteDown.add(node);
                Collections.reverse(nodesToWriteDown);
                for (Node nodeToWrite : nodesToWriteDown) {
                    if (toggle %2 == 0) {
                        depth = nodeToWrite.getDepth() + 1;
                        System.out.print(depth + ":" + nodeToWrite.getAtribut() + "=");
                        branch += depth + ":" + nodeToWrite.getAtribut() + "=";
                    }else {
                        if (nodeToWrite.getChildNode() == null) {
                            System.out.print(nodeToWrite.getState() + " " + nodeToWrite.getAtribut());
                            branch += nodeToWrite.getState() + " " + nodeToWrite.getAtribut();
                            System.out.println();
                            branches.add(branch);
                        }else{
                            depth = nodeToWrite.getDepth() + 1;
                            System.out.print(nodeToWrite.getState() + " " + depth + ":" + nodeToWrite.getAtribut() + "=");
                            branch += nodeToWrite.getState() + " " + depth + ":" + nodeToWrite.getAtribut() + "=";
                            toggle++;
                        }

                    }
                    toggle++;
                }
            }
        }
    }

    //pravim tree po uzoru za prvi labos
    private static void id3() {
        double mainEntropy = 0;
        Set<String> prijelazi = new HashSet<>();
        int maxIndex = 0;
        Node newNodeAttribute = null;
        Node nodeToRemove = null;
        String subEnt = null;
        int depthReached = 0;
        boolean breakDepth = false;

        while (dataFromFile != null && !breakDepth) {
            if (tree.isEmpty()) {
                mainEntropy = -Double.parseDouble(subEntropy (null));
                maxIndex = findRootForTree(mainEntropy, null);
                for (int i = 1; i<dataFromFile.size(); i++) {
                    prijelazi.add(dataFromFile.get(i).split(",")[maxIndex]);
                }
                Node root = new Node (dataFromFile.get(0).split(",")[maxIndex], null, null, maxIndex, null, null);
                tree.add(root);
                Node prijelaz = null;
                Set<Node> children = new HashSet<>();
                for (String item : prijelazi) {
                    prijelaz = new Node (null, item, root, maxIndex, null, null);
                    children.add(prijelaz);
                    tree.add(prijelaz);
                    root.setChildNode(prijelaz);
                }
                root.setChildren(children);
                if (root.getDepth() +1 == border) {
                    addClassifications();
                    break;
                }
            }
            for (Node node : tree) {
                prijelazi = new HashSet<>();
                if (node.getAtribut() == null) {
                    subEnt = subEntropy (node);
                    if (!subEnt.contains(":")) {
                        mainEntropy = -(Double.parseDouble(subEnt));
                        maxIndex = findRootForTree(mainEntropy, node);
                        // TO DO: osigurat da mi se neki atributi ne ponavljaju sa maximalnim IG
                        for (int i = 1; i < dataFromFile.size(); i++) {
                            prijelazi.add(dataFromFile.get(i).split(",")[maxIndex]);
                        }
                        nodeToRemove = node;
                        //node.setAtribut(dataFromFile.get(0).split(",")[maxIndex]);
                        //newNodeAttribute = new Node(dataFromFile.get(0).split(",")[maxIndex], node.getState(), node.getParent());
                        //tree.remove(nodeToRemove);
                        //tree.add(newNodeAttribute);
                        Node prijelaz;
                        Set<Node> children = new HashSet<>();
                        for (String item : prijelazi) {
                            prijelaz = new Node (null, item, node, maxIndex, null, null);
                            if ((prijelaz.getDepth() -1 < border && border>-1) || border == -1) {
                                children.add(prijelaz);
                                tree.add(prijelaz);
                            }
                            else{
                                breakDepth = true;
                                addClassifications();
                                break;
                            }
                            node.setChildNode(prijelaz);
                            node.setChildren(children);
                        }
                        if (breakDepth == false)
                            node.setAtribut(dataFromFile.get(0).split(",")[maxIndex]);
                        break;
                    }else if (subEnt.contains(":")) {
                        List<Integer> lineToRemove = new LinkedList<>();
                        List<String> dataFromFileCopy = new LinkedList<>();
                        List<String> statesToRemove = new LinkedList<>();
                        //ovo znaci da je neki state od nodea dao entropiju 0
                        //dakle treba ga razrijesit
                        dataFromFileCopy.add(dataFromFile.get(0));
                        boolean ok = false;
                        LinkedList<Integer> okIndex = new LinkedList<>(); // koje indexe treba izbacit iz fajla
                        Node current = node;
                        while (current.getParent() != null) {
                            statesToRemove.add(current.getState() + ":" + current.getNoIndexAppear());
                            current = current.getParent();
                        }
                        for (int i = 1; i<dataFromFile.size(); i++) {
                            for (String stateToCheck : statesToRemove) {
                                if (dataFromFile.get(i).split(",")[Integer.parseInt(stateToCheck.split(":")[1])].contains(stateToCheck.split(":")[0])) {
                                    ok = true;
                                }else {
                                    ok = false;
                                    break;
                                }
                            }
                            if (ok) {
                                okIndex.add(i);
                            }
                        }
                        for (int i = 1; i<dataFromFile.size(); i++) {
                            if (!okIndex.contains(i))
                                dataFromFileCopy.add(dataFromFile.get(i));
                        }
                        dataFromFile = new ArrayList<>();
                        for (String newLine : dataFromFileCopy) {
                            dataFromFile.add(newLine);
                        }
                        if (dataFromFile.size() == 1)
                            dataFromFile = null;
                        node.setAtribut(subEnt.split(":")[1]);
                        break;
                    }
                }
            }

        }
        //dakle prvo probati micati elemente iz data Seta
        //onda probati sa while om
        //na poslijetku vidjeti kako slati state-ove od nodova da bi nasao klasifikator > rainy, hot, normal, strong > no (kako poslati sva 4 clana da bi
        //doznao klasifikator!!!???
        //id3();
    }

    private static void addClassifications() {
        List<Integer> lineContains = null;
        String klasifikator;
        String veciKlas = null;
        int max = 0;
        String maxKey = "";
        Map<String, Integer> ponavljanja = new HashMap<>();
        for (Node node : tree) {
            ponavljanja = new HashMap<>();
            max = 0;
            if (node.getAtribut() == null) {

                lineContains = lineContainsValues(node);
                for (Integer i : lineContains) {
                    klasifikator = dataFromFile.get(i).split(",")[noOfAtributes - 1];
                    if (ponavljanja.keySet().contains(klasifikator)) {
                        int current = ponavljanja.get(klasifikator) + 1;
                        ponavljanja.put (klasifikator, current);
                    }else {
                        ponavljanja.put(klasifikator, 1);
                    }
                }
                int ukupno = 0;
                for (String key : ponavljanja.keySet()) {
                    ukupno += ponavljanja.get(key);
                }
                boolean isto = false;
                for (String key : ponavljanja.keySet()) {
                    double dijeli = (double) ukupno/ponavljanja.size();
                    if (dijeli == ponavljanja.get(key) && (ponavljanja.size() > 1)) {
                        isto = true;
                    }
                    maxKey = key;
                    break;
                }

                if (!isto)
                    for (String key : ponavljanja.keySet()) {
                        if (ponavljanja.get(key) > max) {
                            max = ponavljanja.get(key);
                            veciKlas = key;
                            node.setAtribut(veciKlas);
                        }
                    }
                else {
                    for (String key : ponavljanja.keySet()) {
                        if (key.compareTo(maxKey) < 0) {
                            maxKey = key;
                            node.setAtribut(maxKey);
                        }
                    }
                }

                System.out.println(node.getAtribut() + " " + node.getState());
            }
        }
    }

    public static List<Integer> lineContainsValues (Node state) {
        Node currentNode = state;
        List<String> statesToCheck = new LinkedList<>();
        List<Integer> okIndex = new LinkedList<>();
        while (currentNode.getParent() != null) {
            statesToCheck.add(currentNode.getState() + ":" + currentNode.getNoIndexAppear());
            currentNode = currentNode.getParent();
        }
        boolean ok = false;
        for (int i = 1; i<dataFromFile.size(); i++) {
            for (String stateToCheck : statesToCheck) {
                if (dataFromFile.get(i).split(",")[Integer.parseInt(stateToCheck.split(":")[1])].contains(stateToCheck.split(":")[0])) {
                    ok = true;
                }else {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                okIndex.add(i);
            }
        }
        return okIndex;
    }

    private static String subEntropy(Node state) {
        Set<String> classifications = new HashSet<>();
        Map<String, Integer> ponavljanja = new HashMap<>();
        String klasifikator = null;
        double entropy = 0;
        int brojPonavljanja = 0;
        List<Integer> okIndex = new LinkedList<>();
        if (state != null)
            okIndex = lineContainsValues(state); //dakle vidi mi di imaju povezane vrijednosti atribura npr wind:string i weathe:rainy
        else {
            for (int i = 1; i<dataFromFile.size(); i++)
                okIndex.add(i);
        }
        for (Integer i : okIndex) {
            klasifikator = dataFromFile.get(i).split(",")[noOfAtributes-1];
            if (ponavljanja.keySet().contains(klasifikator)) {
                int current = ponavljanja.get(klasifikator) + 1;
                ponavljanja.put (klasifikator, current);
            }else {
                ponavljanja.put(klasifikator, 1);
            }
        }
        for (Integer val : ponavljanja.values()) {
            brojPonavljanja += val;
        }
        for (String key : ponavljanja.keySet()) {
            double omjer = ponavljanja.get(key);
            double omjer2 = omjer / brojPonavljanja;
            entropy +=(omjer2) * (Math.log(omjer2) / Math.log(2));
        }
        if (entropy == 0) {
            for (String key : ponavljanja.keySet()) {
                if (ponavljanja.get(key) != 0) {
                    return ponavljanja.get(key).toString() + ":" + key;
                }
            }
        }
        return Double.toString(entropy);
    }

    private static int findRootForTree (double mainEntropy, Node node) {
        int maxIndex = 0;
        Set<String> prijelazi = new HashSet<>();
        String valueOfAttr = null;
        double maxInformationGain = 0;
        double[] entropyOfAttributes = new double[noOfAtributes-1];
        String currentCandidat = null;
        Node currentNode = null;
        if (node != null)
            currentNode = node.getParent();
        boolean ok = true;
        for (int i = 0; i<noOfAtributes-1; i++) {
            entropyOfAttributes[i] = nodeEntropy (i, node);
            System.out.println("IG(" + dataFromFile.get(0).split(",")[i] + ") " +  (mainEntropy - entropyOfAttributes[i]));
        }
        for (int i = 0; i<entropyOfAttributes.length; i++) {
            ok = true;
            if ((mainEntropy - entropyOfAttributes[i]) > maxInformationGain) {
                if (currentNode != null) {
                    currentCandidat = dataFromFile.get(0).split(",")[i];
                    while (currentNode != null) {
                        if (currentNode.getAtribut().equals(currentCandidat)) {
                            ok = false;
                            break;
                        }
                        currentNode = currentNode.getParent();
                    }
                }
                if (ok) {
                    maxInformationGain = mainEntropy - entropyOfAttributes[i];
                    maxIndex = i;
                }
            }
        }
        usedAttributes.add(maxIndex);
        System.out.println(dataFromFile.get(0).split(",")[maxIndex]);
        /*for (int i = 1; i<dataFromFile.size(); i++) {
            valueOfAttr = dataFromFile.get(i).split(",")[maxIndex];
            prijelazi.add(valueOfAttr);
        }*/
        return maxIndex;
    }

    private static double nodeEntropy(int ordinalNumberOfAttr, Node nodeForIndexAndState) {
        //nije dobro jer ja uzimam u obzir samo yes no razliku
        //a ne uzmem u obzir razlicitors sunny cloudy i rainy npr...
        int brojPonavljanja = 0;
        Map<String, Integer> brojPonavljanjaYes = new HashMap<>();
        double entropy = 0, pYes, pNo;
        int currentValue = 0;
        int currentValueYes = 0, brNo = 0;
        Map<String, Map<String, Integer>> ponavljanjaPoAtributu = new HashMap<>();

        Map<String, Integer> ponavljanja = new HashMap<>();
        String klasifikator = null;

        String valueOfAttr = null;
        int podijeliSa = 0;
        //ne zaboraviti implementirati za maybe
        int podijeliSaBrojemPonavljanja = 0; // znaci kada racznam IG onda mi treba omjer kolko se pojavljavalo toga u setu
        for (int i = 1; i<dataFromFile.size(); i++) {

            valueOfAttr = dataFromFile.get(i).split(",")[ordinalNumberOfAttr];
            if ((nodeForIndexAndState != null && dataFromFile.get(i).split(",")[nodeForIndexAndState.getNoIndexAppear()].equals(nodeForIndexAndState.getState())) || nodeForIndexAndState == null) {
                podijeliSa++;
                klasifikator = dataFromFile.get(i).split(",")[noOfAtributes-1];
                ponavljanja = new HashMap<>();

                if (ponavljanjaPoAtributu.keySet().contains(valueOfAttr)) {
                    ponavljanja = ponavljanjaPoAtributu.get(valueOfAttr);
                    if (ponavljanja.containsKey(klasifikator)) {
                        int current = ponavljanja.get(klasifikator) + 1;
                        ponavljanja.put(klasifikator, current);
                        ponavljanjaPoAtributu.put(valueOfAttr, ponavljanja);
                    }else {
                        ponavljanja.put(klasifikator, 1);
                        ponavljanjaPoAtributu.put(valueOfAttr, ponavljanja);
                    }
                }else {
                    ponavljanja.put(klasifikator, 1);
                    ponavljanjaPoAtributu.put(valueOfAttr, ponavljanja);
                }
            }
        }

        double ig = 0;
        for (String key : ponavljanjaPoAtributu.keySet()) {
            entropy = 0;
            ponavljanja = ponavljanjaPoAtributu.get(key);
            brojPonavljanja = 0;
            for (Integer val : ponavljanja.values()) {
                brojPonavljanja += val;
            }
            double omjer2 = 0;
            for (String keyKlasif : ponavljanja.keySet()) {
                double brojPonavljanjaUSetu = ponavljanja.get(keyKlasif);
                omjer2 = brojPonavljanjaUSetu / brojPonavljanja;
                //entropy +=((omjer2) * (Math.log(omjer2) / Math.log(2))) * (omjer2);
                entropy += (omjer2) * (Math.log(omjer2) / Math.log(2));
            }
            double omjer3 = (double) brojPonavljanja/podijeliSa;
            ig += entropy * (omjer3);
        }
        return -(ig);
    }
    private static void readCSV(String file, String testFile) {
        String line = "";
        String splitBy = ",";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                dataFromFile.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        line = "";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(testFile));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                dataFromTestFile.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

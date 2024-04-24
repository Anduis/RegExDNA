import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String ER = sc.next();
    Thomson thom = new Thomson(new Scanner(ER));
    Discrimina ds = new Discrimina(new Scanner(thom.salida));
    Main conjunto = new Main(ds);// epsilons agrupados
    conjunto.text = sc.next();
    // sobre los automatas
    // System.out.println("alphas:\n"+conjunto.alphas);
    // System.out.println("epsilons:\n"+conjunto.epsilons);
    // System.out.println("inicio: "+conjunto.first);
    // System.out.println("final: "+conjunto.last);
    // System.out.println("Estados agrupados:");
    // conjunto.printConjunto();
    // conjunto.printDelta();
    System.out.println("*-*-*-*-*-*");
    System.out.println("\u001B[32m" + ER + "\u001B[0m");
    // System.out.println(conjunto.text);
    // conjunto.recorre();
    conjunto.Main();
  }

  /*
   * colores disponibles ANSI
   * RESET = "\u001B[0m";
   * BLACK = "\u001B[30m";
   * RED = "\u001B[31m";
   * GREEN = "\u001B[32m";
   * YELLOW ="\u001B[33m";
   * BLUE = "\u001B[34m";
   * PURPLE ="\u001B[35m";
   * CYAN = "\u001B[36m";
   * WHITE = "\u001B[37m";
   */
  String COLOR = "\u001B[35m";
  String RESET = "\u001B[0m";
  NodoLista head;
  String epsilons;
  String alphas;
  String text;
  int last;
  int first;
  int[][] del;
  String abc = "ACGT";

  void recorre() {
    NodoLista sel;
    int ocurrence = 0;
    int i = 0;
    int j = 0;
    int[] sal;
    while (i < text.length()) {
      sal = new int[2];
      sel = find(first);
      j = 0;
      if (del[sel.id][abc.indexOf(text.charAt(i))] != -1) {
        while (del[sel.id][abc.indexOf(text.charAt(i))] != -1) {
          System.out.print(i + " " + text.charAt(i) + " D[" + sel.id + "]" + " P["
              + del[sel.id][abc.indexOf(text.charAt(i))] + "]" + (j - (sal[1] - sal[0])));
          sel = getById(del[sel.id][abc.indexOf(text.charAt(i))]);
          j++;
          if (sel.fl == 'l') {
            ocurrence++;
            sal[0] = i - j;
            sal[1] = i;
          }
          System.out.println("-");
          i++;
          if (i < text.length())
            if (del[sel.id][abc.indexOf(text.charAt(i))] == -1 && (j - (sal[1] - sal[0])) != 0)
              System.out.print("");
          if (i >= text.length())
            break;
        }
      }
      if (!(sal[0] == 0 && 0 == sal[1])) {
        System.out.println("--" + (" o[" + sal[0] + "," + sal[1] + "]") + (j - (sal[1] - sal[0])));
        i = i - (j - (sal[1] - sal[0]));
      }
      sel = find(first);
      if (i >= text.length())
        break;
      if (del[sel.id][abc.indexOf(text.charAt(i))] == -1) {
        System.out.println(i + " " + text.charAt(i));
        i++;
      }
    }
    System.out.println(ocurrence);
  }

  void Main() {
    NodoLista sel;
    int ocurrence = 0;
    int i = 0;
    int j = 0;
    int[] sal;
    String ans = "";
    while (i < text.length()) {
      sal = new int[2];
      sel = find(first);
      j = 0;
      if (del[sel.id][abc.indexOf(text.charAt(i))] != -1)
        while (del[sel.id][abc.indexOf(text.charAt(i))] != -1) {
          sel = getById(del[sel.id][abc.indexOf(text.charAt(i))]);
          j++;
          if (sel.fl == 'l') {
            ocurrence++;
            sal[0] = i - j;
            sal[1] = i;
          }
          i++;
          if (i < text.length())
            if (del[sel.id][abc.indexOf(text.charAt(i))] == -1 && (j - (sal[1] - sal[0])) != 0)
              ans += text.charAt(i - 1);
          if (i >= text.length())
            break;
        }
      ans += COLOR + text.substring(sal[0] + 1, sal[1] + 1) + RESET;
      if (!(sal[0] == 0 && 0 == sal[1])) {
        i = i - (j - (sal[1] - sal[0]));
      }
      sel = find(first);
      if (i < text.length())
        if (del[sel.id][abc.indexOf(text.charAt(i))] == -1)
          ans += (text.charAt(i++));
    }
    System.out.println(ans);
    System.out.println(ocurrence);
  }

  Main(Discrimina ds)// construye una lista donde cada nodo es un estado epsilon
  {
    alphas = ds.alphas;
    epsilons = ds.epsilons;
    first = ds.ini;
    last = ds.fin;
    includeEpsilon(new Scanner(epsilons));
    includeAlpha();
  }

  boolean checkUnicity()// agrupa todas las listas que tengan al menos un elemento comun
  {
    boolean ans = false;
    NodoLista externo = head;
    while (externo.hasNext()) {
      NodoLista interno = externo.sig;
      while (interno != null) {
        if (hasIntersection(externo.lista, interno.lista)) {
          bond(externo, interno);
          ans = true;
        }
        interno = interno.sig;
      }
      if (externo.hasNext())
        externo = externo.sig;
    }
    return ans;
  }

  boolean hasIntersection(Lista a, Lista b)// entre dos listas
  {
    Nodo tempB = b.head;
    while (tempB != null) {
      if (a.isIn(tempB.entero))
        return true;
      tempB = tempB.sig;
    }
    return false;
  }

  NodoLista extract(Main x, NodoLista a)// dado nodo elimina sus enlaces de x
  {
    if (a == x.head)
      x.head = x.head.sig;
    else {
      NodoLista temp = x.head;
      while (temp.sig != a)
        temp = temp.sig;
      temp.sig = a.sig;
    }
    return a;
  }

  NodoLista find(int x) {
    NodoLista temp = head;
    while (temp != null) {
      if (temp.lista.isIn(x))
        return temp;
      temp = temp.sig;
    }
    return null;
  }

  NodoLista getById(int x) {
    NodoLista temp = head;
    while (temp != null) {
      if (temp.id == x)
        return temp;
      temp = temp.sig;
    }
    return null;
  }

  NodoLista tail() {
    NodoLista temp = head;
    while (temp.sig != null)
      temp = temp.sig;
    return temp;
  }

  void addEpsilon(int x, int y)// dado dos enteros revisa si alguno de ellos esta en alguna lista
  {
    Lista s = new Lista();
    s.addNodo(x);
    s.addNodo(y);
    NodoLista u = new NodoLista(s);
    if (head == null) {
      head = u;
      return;
    }
    NodoLista temp = head;
    boolean wasAdded = false;
    while (temp != null) {
      wasAdded = false;
      if (temp.lista.isIn(x)) {
        if (!temp.lista.isIn(y)) {
          temp.lista.addNodo(y);
          wasAdded = true;
          break;
        }
      }
      if (temp.lista.isIn(y)) {
        if (!temp.lista.isIn(x)) {
          temp.lista.addNodo(x);
          wasAdded = true;
          break;
        }
      }
      temp = temp.sig;
    }
    if (!wasAdded) {
      if (head.sig == null)
        head.sig = u;
      else
        tail().sig = u;
    }
  }

  void addNodoLista(NodoLista copy)// agrega nodo a en conjunto
  {
    NodoLista a = copy;
    a.sig = null;
    if (head == null) {
      head = a;
      return;
    } else if (head.sig == null)
      head.sig = a;
    else
      tail().sig = a;
  }

  void merge(Main a, Main b)// todas las listas en b las pasa a a
  {
    while (b.head != null)
      a.addNodoLista(extract(b, b.head));
  }

  void bond(NodoLista a, NodoLista b)// deja en a todos los numeros singulares
  {
    Nodo tempB = b.lista.head;
    while (tempB != null) {
      if (!a.lista.isIn(tempB.entero))
        a.lista.addNodo(tempB.entero);
      tempB = tempB.sig;
    }
    extract(this, b);
  }

  void add(int x) {
    Lista s = new Lista();
    s.addNodo(x);
    addNodoLista(new NodoLista(s));
  }

  void includeAlpha() {
    addSingle(new Scanner(alphas));
    alphaMerge(new Scanner(alphas));
    find(last).fl = 'l';
    find(first).fl = 'f';
    del = delta(new Scanner(alphas));
  }

  void includeEpsilon(Scanner ep) {
    while (ep.hasNextInt())
      addEpsilon(ep.nextInt(), ep.nextInt());
    while (checkUnicity()) {
    }
  }

  void setId() {
    int id = 0;
    NodoLista temp = head;
    while (temp != null) {
      temp.id = id++;
      temp = temp.sig;
    }
  }

  void printConjunto()// imprime todas las listas
  {
    NodoLista temp = head;
    while (temp != null) {
      System.out.print("[" + temp.id);
      if (temp.fl != 'n')
        System.out.print(temp.fl + "]");
      else
        System.out.print(" ]");
      temp.lista.printLista();
      temp = temp.sig;
    }
  }

  void addSingle(Scanner sc) {
    while (sc.hasNextLine()) {
      String currentLine = sc.nextLine();
      String[] separado = currentLine.split(" ");
      if (find(Integer.parseInt(separado[0])) == null)
        add(Integer.parseInt(separado[0]));
      if (find(Integer.parseInt(separado[2])) == null)
        add(Integer.parseInt(separado[2]));
    }
  }

  void alphaMerge(Scanner sc) {
    setId();
    String[] unidos = alphaSet(sc);
    for (int i = 0; i < unidos.length; i++) {
      Scanner ss = new Scanner(unidos[i]);
      int[] temp = { -1, -1, -1, -1 };
      while (ss.hasNextLine()) {
        String currentLine = ss.nextLine();
        String[] separado = currentLine.split(" ");
        if (temp[abc.indexOf(separado[1].charAt(0))] == -1)
          temp[abc.indexOf(separado[1].charAt(0))] = Integer.parseInt(separado[2]);
        else
          addEpsilon(temp[abc.indexOf(separado[1].charAt(0))], Integer.parseInt(separado[2]));
      }
      ss.close();
      setId();
    }
    checkUnicity();
  }

  void printDelta() {
    System.out.println("\n   |A|C|G|T|");
    for (int i = 0; i < del.length; i++) {
      System.out.print("[" + i + "] ");
      for (int j = 0; j < del[0].length; j++) {
        if (del[i][j] != -1)
          System.out.print(del[i][j] + " ");
        else
          System.out.print("- ");
      }
      System.out.println();

    }
  }

  String[] alphaSet(Scanner sc) {
    String[] ans = new String[tail().id + 1];
    for (int i = 0; i < ans.length; i++)
      ans[i] = "";
    while (sc.hasNextLine()) {
      String currentLine = sc.nextLine();
      String[] separado = currentLine.split(" ");
      ans[find(Integer.parseInt(separado[0])).id] += currentLine + "\n";
    }
    /*
     * for (int i = 0 ; i< ans.length ; i++)
     * if(ans[i] != "")
     * ans[i].substring(0, ans[i].length()-1);
     */
    return ans;
  }

  int[][] delta(Scanner sc) {
    setId();
    int[][] ans = new int[tail().id + 1][4];
    for (int i = 0; i < ans.length; i++)
      for (int j = 0; j < ans[i].length; j++)
        ans[i][j] = -1;
    while (sc.hasNextLine()) {
      String currentLine = sc.nextLine();
      String[] separado = currentLine.split(" ");
      ans[find(Integer.parseInt(separado[0])).id][abc
          .indexOf(separado[1].charAt(0))] = find(Integer.parseInt(separado[2])).id;
    }
    return ans;
  }
}

class NodoLista {
  Lista lista;
  NodoLista sig;
  char fl = 'n';
  int id = 0;

  NodoLista() {
  }

  NodoLista(Lista x) {
    lista = x;
  }

  boolean hasNext() {
    if (sig != null)
      return true;
    return false;
  }
}

class Lista {
  Nodo head;
  Nodo tail;

  Lista() {
  }

  void addNodo(int s) {
    Nodo x = new Nodo(s);
    if (head == null) {
      head = x;
      return;
    } else if (head.sig == null)
      head.sig = x;
    else
      tail.sig = x;
    tail = x;
  }

  void printLista() {
    Nodo temp = head;
    while (temp != null) {
      System.out.print(" ");
      System.out.print(temp.entero);
      temp = temp.sig;
    }
    System.out.println();
  }

  boolean isIn(int x) {
    Nodo temp = head;
    while (temp != null) {
      if (x == temp.entero)
        return true;
      temp = temp.sig;
    }
    return false;
  }
}

class Nodo {
  int entero;
  Nodo sig;

  Nodo(int x) {
    entero = x;
  }

  boolean isThis(int y) {
    if (y == entero)
      return true;
    return false;
  }

  boolean hasNext() {
    if (sig != null)
      return true;
    return false;
  }
}

class Discrimina {
  String abc = "ACGT";
  String epsilons = "";
  String alphas = "";
  int ini = 0;
  int fin = 0;
  int rows = 0;

  Discrimina(Scanner sc) {
    while (sc.hasNextLine()) {
      String currentLine = sc.nextLine();
      String[] separado = currentLine.split(" ");
      if (separado.length == 3) {
        if (abc.indexOf(separado[1].charAt(0)) != -1)
          alphas += (separado[0] + " " + separado[1] + " " + separado[2] + "\n");
        if (separado[1].charAt(0) == 'e')
          epsilons += (separado[0] + " " + separado[2] + "\n");
        rows++;
      } else
        fin = Integer.parseInt(separado[0]) - 1;
    }
    alphas = alphas.substring(0, alphas.length() - 1);
    epsilons = epsilons.substring(0, epsilons.length() - 1);
    ini = findIni();
  }

  int findIni() {
    int ans = 0;
    int[] izquierdos = new int[rows];
    int[] derechos = new int[rows];
    int i = 0;
    int d = 0;
    String[] alfas = alphas.split("\n");
    for (String s : alfas) {
      String[] temp = s.split(" ");
      for (int j = 0; j < 3; j++)
        if (abc.indexOf(temp[j].charAt(0)) == -1)
          if (j == 0)
            izquierdos[i++] = Integer.parseInt(temp[0]);
          else
            derechos[d++] = Integer.parseInt(temp[2]);
    }
    String[] epis = epsilons.split("\n");
    for (String s : epis) {
      String[] temp = s.split(" ");
      izquierdos[i++] = Integer.parseInt(temp[0]);
      derechos[d++] = Integer.parseInt(temp[1]);
    }
    for (int x : izquierdos) {
      boolean isUnique = true;
      for (int y : derechos)
        if (y == x)
          isUnique = false;
      if (isUnique)
        ans = x;
    }
    return ans;
  }
}

class Thomson {
  int i = 0;
  String s = "";
  int pos = 0;
  int len;
  String salida = "";

  Thomson(Scanner sc) {
    s = sc.next();
    len = s.length();
    int[] mar = E();
    salida += i + "\n";
    mar[0] = mar[0];// para que visualstudio no se queje
  }

  void plr(int x, char y, int z) {
    salida += (x + " " + y + " " + z) + "\n";
  }

  int[] E() {
    int[] temp = T();
    if (pos < len && s.charAt(pos) == '|') {
      pos++;
      int[] timp = E();
      int[] re = { i++, i++ };
      plr(re[0], 'e', temp[0]);
      plr(re[0], 'e', timp[0]);
      plr(temp[1], 'e', re[1]);
      plr(timp[1], 'e', re[1]);
      return re;
    } else
      return temp;
  }

  int[] T() {
    int[] temp = P();
    if (pos < len && s.charAt(pos) == '.') {
      pos++;
      int[] timp = T();
      int[] re = { temp[0], timp[1] };
      plr(temp[1], 'e', timp[0]);
      return re;
    } else
      return temp;
  }

  int[] P() {
    int[] temp = F();
    if (pos < len && s.charAt(pos) == '*') {
      int[] re = { i++, i++ };
      plr(re[0], 'e', re[1]);
      plr(re[0], 'e', temp[0]);
      plr(temp[1], 'e', re[1]);
      plr(temp[1], 'e', temp[0]);
      pos++;
      return re;
    } else
      return temp;
  }

  int[] F() {
    if (isIn(s.charAt(pos))) {
      int[] re = { i++, i++ };
      plr(re[0], s.charAt(pos), re[1]);
      pos++;
      return re;
    } else if (pos < len && s.charAt(pos) == '(') {
      pos++;// lee '('
      int[] temp = E();
      if (s.charAt(pos) == ')') {
        pos++;// lee ')'
        return temp;
      }
      System.exit(1);
    }
    return new int[2];
  }

  boolean isIn(char c) {
    if (c == 'A' | c == 'C' | c == 'G' | c == 'T')
      return true;
    return false;
  }
}

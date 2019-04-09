// Jepardy

int number_of_prizes = 0;

int cat = -1;
int pnt = -1;
int mode = 0;

float sclx,scly;

class question {
  int points;
  String quest,anwser;
  question(int p,String q,String a) {
    points = p;
    quest = q;
    anwser = a;
  }
}

class category {
  String name;
  question[] quests = {};
  category(String lbl) {
    name = lbl;
  }
  void addQuestion(question q) {
    // dumb workaround
    question[] Temp = new question[quests.length+1];
    for (int i = 0; i < quests.length; i++) {
      Temp[i] = quests[i];
    }
    Temp[quests.length] = q;
    quests = Temp;
  }
  void pullQuestion(int pnts) { // re-writen to take less time and space
    // get all questions with set points
    int[] hits = {};
    for (int i = 0; i < quests.length; i++) {
      if (quests[i].points == pnts)
        hits = append(hits,i);
    }
    question[] Temp = new question[hits.length];
    for (int i = 0; i < hits.length; i++) {
      Temp[i] = quests[hits[i]];
    }
    int loc = round(random(Temp.length-1)); // randomly choose a valid question
    // repeats are possable and unavoidable (I think)
    current = Temp[loc];
  }
}

category[] categories = {};

question current = null;

void setup() {
  fullScreen(); // fullScreen the canvas
  rectMode(CORNERS); // initalize some characteristics
  textAlign(CENTER);
  textSize(50);
  parseFile(); // extract all of the data from the file
  sclx = (width/(categories.length));
  scly = (height/(number_of_prizes+1));
}

void draw() {
  background(25,0,100);
  if (mode == 0)
    displayCategories();
  else if (mode == 1)
    displayProblem();
  else
    displayAnwser();
}

void displayCategories() {
  fill(25,0,158);
  rect(0,0,width,scly);
  for (int x = 0; x < categories.length; x++) { // transfered some other code from a similar loop to save run time
    float xpos = ((sclx*x)+(sclx*(x+1)))/2; // I will clean this up later
    for (int y = 1; y < number_of_prizes+1; y++) {
      fill(0);
      text(y*100,xpos,((scly*y)+(scly*(y+1)))/2);
      line(0,scly*(y+1),width,scly*(y+1));
    }
    text(categories[x].name,xpos,scly/2);
    line(sclx*(x+1),0,sclx*(x+1),height);
  }
}

void displayProblem() { // displays the current question chosen
  fill(0);
  text(current.quest,width/2,height/2); 
}
void displayAnwser() { // displays the current question's anwser
  fill(0);
  text(current.anwser,width/2,height/2);
}

void mousePressed() {// Ultra-Fast Click Detection™ (Patent Pending)
  if (mode == 0) {
    cat = (int)(mouseX/sclx); // category
    pnt = (int)(mouseY/scly)-1; // point level
   
    // println(cat,pnt); // bug checking
    
    if (pnt != -1) {
      mode = 1;
      category c = categories[cat]; // grabs the current category
      c.pullQuestion(pnt); // randomly choses a question from a point "pool"
    }
  } else {
    mode++;
    mode%=3;
  }
}

void parseFile() { // IO
  BufferedReader reader = createReader("Jeopardy.txt"); // reads the file "Jeopardy.txt"
  String line = null;
  int linenumb = 1; // for bug checker
  try {
    while ((line = reader.readLine()) != null) {
      String[] temp = line.split(" ");
      String id = (String)(temp[0].toLowerCase());
      if (id.equals("points_per_category")) // needed (I think) so we can have the correct number of rows displayed
        number_of_prizes = int(temp[1]);
      else if (id.equals("c")) { // if you want to add a category
        // dumb workaround
        category[] Temp = new category[categories.length+1];
        for (int i = 0; i < categories.length; i++) {
          Temp[i] = categories[i];
        }
        // add support for spaces in text
        String[] name = {};
        for (int i = 1; i < temp.length; i++) {
          name = append(name,temp[i]);
        }
        Temp[categories.length] = new category(join(name," "));
        categories = Temp;
      } else if (id.equals("q")) { // if you want to add a question
        // add support for spaces in text
        int loc = 0;
        for (int i = 0; i < temp.length; i++) {
          if (temp[i].equals("\""))
            loc = i;
        }
        String[] q = {};
        for (int i = 3; i < loc; i++) {
          q = append(q,temp[i]);
        }
        String[] a = {};
        for (int i = loc+1; i < temp.length; i++) {
          a = append(a,temp[i]);
        }
        categories[int(temp[1])].addQuestion(new question(int(temp[2]),join(q," "),join(a," ")));
      } else // if you try to call a command that doesn't exist, throw an argument error
        throw new IllegalArgumentException("Unexpected identifier \""+temp[0]+"\" at "+linenumb);
      linenumb++;
    }
    reader.close();
  } catch (IOException e) {
    e.printStackTrace();
  }
}

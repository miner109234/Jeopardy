import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Jepardy extends PApplet {

// Jepardy

int number_of_prizes = 0;

int cat = -1;
int pnt = -1;
int mode = 0;

class question {
  int points;
  String quest,anwser;
  boolean isOld = false;
  question(int p,String q,String a) {
    points = p;
    quest = q;
    anwser = a;
  }
  public void use() {
    isOld = true;
  }
  public void reset() {
    isOld = false;
  }
}

class category {
  String name;
  question[] quests = {};
  category(String lbl) {
    name = lbl;
  }
  public void addQuestion(question q) {
    // dumb workaround
    question[] Temp = new question[quests.length+1];
    for (int i = 0; i < quests.length; i++) {
      Temp[i] = quests[i];
    }
    Temp[quests.length] = q;
    quests = Temp;
    
  }
  public void pullQuestion(int pnts) {
    // get all questions with set points
    int leng = 0;
    for (int i = 0; i < quests.length; i++) {
      if (quests[i].points == pnts)
        leng++;
    }
    question[] Temp = new question[leng];
    int index = 0;
    for (int i = 0; i < quests.length; i++) {
      if (quests[i].points == pnts) {
        Temp[index] = quests[i];
        index++;
        if (index == Temp.length)
          break;
      }
    }
    int loc = round(random(Temp.length-1));
    // repeats are possable and unavoidable
    current = Temp[loc];
  }
}

category[] categories = {
};

question current = null;

public void setup() {
  
  rectMode(CORNERS);
  textAlign(CENTER);
  textSize(50);
  parseFile();
}

public void draw() {
  if (mode == 0)
    displayCategories();
  else if (mode == 1)
    displayProblem();
  else
    displayAnwser();
}

public void displayCategories() {
  float sclx = (width/(categories.length));
  float scly = (height/(number_of_prizes+1));
  for (int x = 0; x < categories.length; x++) {
    for (int y = 1; y < number_of_prizes+1; y++) {
      fill(25,0,100);
      rect(sclx*x,scly*y,sclx*(x+1),scly*(y+1));
      fill(0);
      text(y*100,((sclx*x)+(sclx*(x+1)))/2,((scly*y)+(scly*(y+1)))/2);
    }
  }
  for (int x = 0; x < categories.length; x++) {
    fill(25,0,158);
    rect(sclx*x,0,sclx*(x+1),scly);
    fill(0);
    text(categories[x].name,((sclx*x)+(sclx*(x+1)))/2,scly/2);
  }
}

public void displayProblem() {
  fill(25,0,100);
  rect(0,0,width,height);
  fill(0);
  text(current.quest,width/2,height/2);
}
public void displayAnwser() {
  fill(25,0,100);
  rect(0,0,width,height);
  fill(0);
  text(current.anwser,width/2,height/2);
}

public void mousePressed() {// Ultra-Fast Click Detection™ (Patent Pending)
  if (mode == 0) {
    float sclx = (width/(categories.length));
    float scly = (height/(number_of_prizes+1));
  
    cat = (int)(mouseX/sclx); // category
    pnt = (int)(mouseY/scly)-1; // point level
   
    println(cat,pnt); // bug checking
    
    if (pnt != -1) {
      mode = 1;
      category c = categories[cat];
      c.pullQuestion(pnt);
    }
  } else if (mode == 1)
    mode = 2;
  else if (mode == 2)
    mode = 0;
}

public void parseFile() { // IO
  BufferedReader reader = createReader("Jeopardy.txt");
  String line = null;
  int linenumb = 1;
  try {
    while ((line = reader.readLine()) != null) {
      String[] temp = line.split(" ");
      String id = (String)(temp[0].toLowerCase());
      if (id.equals("points_per_category"))
        number_of_prizes = PApplet.parseInt(temp[1]);
      else if (id.equals("c")) {
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
      } else if (id.equals("q")) {
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
        categories[PApplet.parseInt(temp[1])].addQuestion(new question(PApplet.parseInt(temp[2]),join(q," "),join(a," ")));
      } else
        throw new IllegalArgumentException("Unexpected identifier \""+temp[0]+"\" at "+linenumb);
      linenumb++;
    }
    reader.close();
  } catch (IOException e) {
    e.printStackTrace();
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Jepardy" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

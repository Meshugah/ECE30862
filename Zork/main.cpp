#include "header.hpp"
using namespace std;
using namespace rapidxml;

int main(int argc, char *argv[]){

    file<> xmlFile(argv[1]);
    
    xml_document<> doc;    // character type defaults to char
    doc.parse<0>(xmlFile.data());    // 0 means default parse flags
    
    xml_node<> *node = doc.first_node(); //map node (root)
    
    //Setup xml lists
    vector<xml_node<>*> roomx;
    vector<xml_node<>*> containerx;
    vector<xml_node<>*> itemx;
    vector<xml_node<>*> creaturex;
    
    for(xml_node<>*top = node -> first_node(); top; top = top -> next_sibling()){
      if(string(top->name()) == string("room")){
        roomx.push_back(top);
      }
      if(string(top->name()) == string("container")){
        containerx.push_back(top);
      }
      if(string(top->name()) == string("item")){
        itemx.push_back(top);
      }
      if(string(top->name()) == string("creature")){
        creaturex.push_back(top);
      }
    }
    
    //Setup Zork list
    //Add rooms to map
    Room *room;
    int i = roomx.size();
    int ind = 0;
    while(ind < i){
      room = new Room(roomx[ind]);
      rooms.push_back(room);
      ind++;
    }
    //Add containers to map
    i = containerx.size();
    ind = 0;
    Container * container;
    while(ind < i){
    	container = new Container(containerx[ind]);
    	containers.push_back(container);
    	ind++;
    }
    //Add items to map
    i = itemx.size();
    ind = 0;
    Item * item;
    while(ind < i){
      item = new Item(itemx[ind]);
      items.push_back(item);
      ind++;
    }
    //Add creatures to map
    i = creaturex.size();
    ind = 0;
    Creature * creature;
    while(ind < i){
      creature = new Creature(creaturex[ind]);
      creatures.push_back(creature);
      ind++;
    }

    //Start game
    string input;
    bool overridden = false;
    current = rooms[0];
    cout<< current -> description <<endl;
    while(true){
        overridden = checkTrigger_nocommand();
        if(END){break;}
        if(overridden){continue;}
        getline(cin, input);
        //Quit Command
        if(string(input) == string("q")){break;}
        
        overridden = checkTrigger_withcommand(input);
        if(END){break;}
        if(overridden){continue;}
        
        checkInput(input);
        if(END){break;}
    }
    return 0;
}
#ifndef _ROOM_H
#define _ROOM_H

#include<iostream>
#include<sstream>
#include<fstream>
#include<cstdlib>
#include<vector>
#include<string>
#include<list>
#include "rapidxml-1.13/rapidxml.hpp"
#include "rapidxml-1.13/rapidxml_utils.hpp"
#include "rapidxml-1.13/rapidxml_print.hpp"
#include "Trigger.hpp"
using namespace std;
using namespace rapidxml;

typedef struct _border{
  string direction;
  string name;
}Border;


class Room{
public:
	string name;
	string status;
	string type;
	string description;
	vector<Border *> border;
	vector<string> container;
	vector<string> item;
	vector<string> creature;
    vector<Trigger*> trigger;
    
	Room(xml_node<>* node){setupRoom(node);}
	virtual ~Room(){};
private:
	void setupRoom(xml_node<>* room){
		for(xml_node<>* kid = room -> first_node();
				kid; kid = kid -> next_sibling()){
			if(string(kid->name()) == "name"){
				name = kid -> value();
			}
			if(string(kid->name()) == "status"){
				status = kid->value();
			}
			if(string(kid->name()) == "type"){
				type = kid -> value();
			}
			if(string(kid->name()) == "description"){
				description = kid -> value();
			}
			if(string(kid->name()) == "border"){
				Border * newborder = new Border();
				string bdname, direction;
				for(xml_node<>* kid2 = kid -> first_node();
						kid2; kid2 = kid2 -> next_sibling()){
					if(string(kid2->name()) == "name"){
						bdname = kid2 -> value();
					}
					if(string(kid2->name()) == "direction"){
						direction = kid2->value();
						if(direction == "north"){direction = "n";}
						if(direction == "south"){direction = "s";}
						if(direction == "west"){direction = "w";}
						if(direction == "east"){direction = "e";}
					}
				}
				newborder -> direction = direction;
				newborder -> name = bdname;
				border.push_back(newborder);
			}
			string buffer;
			if(string(kid->name()) == "container"){
				buffer = kid -> value();
				container.push_back(buffer);
			}
			if(string(kid->name()) == "item"){
				buffer = kid -> value();
				item.push_back(buffer);
			}
			if(string(kid->name()) == "creature"){
				buffer = kid -> value();
				creature.push_back(buffer);
			}
			if(string(kid->name()) == "trigger"){
                Trigger * t = new Trigger(kid);
                trigger.push_back(t);
			}
       }
	}
};

#endif
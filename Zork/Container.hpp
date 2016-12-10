#ifndef _CONTAINER_H
#define _CONTAINER_H

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

class Container{
public:
	string name;
    string status;
    string description;
    vector<string> item;
    vector<string> accept;
    vector<Trigger*> trigger;
    
	Container(xml_node<>* node){
		setupContainer(node);
	}
	virtual ~Container(){};
private:
	void setupContainer(xml_node<>* node){
		for(xml_node<>* kid = node -> first_node();
				kid; kid = kid -> next_sibling()){
			if(string(kid->name()) == "name"){
				name = kid -> value();
			}
            if(string(kid->name()) == "status"){
                status = kid -> value();
            }
            if(string(kid->name()) == "description"){
                description = kid -> value();
            }
            string buffer;
            if(string(kid->name()) == "item"){
                buffer = kid -> value();
                item.push_back(buffer);
            }
            if(string(kid->name()) == "accept"){
                buffer = kid -> value();
                accept.push_back(buffer);
            }
            if(string(kid->name()) == "trigger"){
                Trigger * t = new Trigger(kid);
                trigger.push_back(t);
            }
		}
	}
protected:
};

#endif
#ifndef _CREATURE_H
#define _CREATURE_H

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
#include "Attack.hpp"
using namespace std;
using namespace rapidxml;

class Creature{
public:
    string name;
    string status;
    string description;
    vector<string> vulnerability;
    Attack *attack;
    vector<Trigger*> trigger;
    
	Creature(xml_node<>* node){
        setupCreature(node);
    }
	virtual ~Creature(){};
private:
    void setupCreature(xml_node<>* node){
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
            if(string(kid->name()) == "vulnerability"){
                buffer = kid -> value();
                vulnerability.push_back(buffer);
            }
            if(string(kid->name()) == "attack"){
                attack = new Attack(kid);
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
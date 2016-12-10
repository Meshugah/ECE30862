#ifndef _TRIGGER_H
#define _TRIGGER_H

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

using namespace std;
using namespace rapidxml;

typedef struct _status{
    string object;
    string status;
}Status;

typedef struct _owner{
    string object;
    string has;
    string owner;
}Owner;

class Trigger{
public:
    string type;
    string command;
    string print;
    vector<string> action;
    int condition;//2 is status, 3 is owner
    bool has_command;
    bool has_print;
    bool has_action;
    
    Status status;
    Owner owner;
    
    int times;//for single
	Trigger(xml_node<>* node){
        setupTrigger(node);
	}
	virtual ~Trigger(){};
private:
    void setupTrigger(xml_node<>* node){
        has_command = false;
        has_action = false;
        has_print = false;
        type = "single";
        times = 0;
        for(xml_node<>* kid = node -> first_node();
            kid; kid = kid -> next_sibling()){
            if(string(kid->name()) == "type"){
                type = kid -> value();
            }
            if(string(kid->name()) == "print"){
                has_print = true;
                print = kid -> value();
            }
            if(string(kid->name()) == "command"){
                has_command = true;
                command = kid -> value();
            }
            if(string(kid->name()) == "action"){
                has_action = true;
                string buffer = kid -> value();
                action.push_back(buffer);
            }
            if(string(kid->name()) == "condition"){
                condition = condition_count(kid);
                if(condition == 2){
                    setupStatus(kid);
                }else if(condition == 3){
                    setupOwner(kid);
                }
            }
        }
    }
    
    int condition_count(xml_node<>* node){
        int num = 0;
        for(xml_node<>* kid = node -> first_node();
            kid; kid = kid -> next_sibling()){
            num++;
        }
        return num;
    }
    void setupStatus(xml_node<>* node){
        for(xml_node<>* kid = node -> first_node();
            kid; kid = kid -> next_sibling()){
            if(string(kid->name()) == "object"){
                status.object = kid -> value();
            }
            if(string(kid->name()) == "status"){
                status.status = kid -> value();
            }
        }
    }
    void setupOwner(xml_node<>* node){
        for(xml_node<>* kid = node -> first_node();
            kid; kid = kid -> next_sibling()){
            if(string(kid->name()) == "object"){
                owner.object = kid -> value();
            }
            if(string(kid->name()) == "has"){
                owner.has = kid -> value();
            }
            if(string(kid->name()) == "owner"){
                owner.owner = kid -> value();
            }
        }
    }
};

#endif
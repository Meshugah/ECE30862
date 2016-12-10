#ifndef _HEADER_H
#define _HEADER_H

#include<iostream>
#include<fstream>
#include<cstdlib>
#include<vector>
#include<string>
#include<list>
#include "rapidxml-1.13/rapidxml.hpp"
#include "rapidxml-1.13/rapidxml_utils.hpp"
#include "rapidxml-1.13/rapidxml_print.hpp"
#include "Room.hpp"
#include "Container.hpp"
#include "Item.hpp"
#include "Creature.hpp"

using namespace std;
using namespace rapidxml;

//Global Declaration
vector<Room*> rooms;
vector<Container*> containers;
vector<Item*> items;
vector<Creature*> creatures;

vector<string> inventory;
bool END = false;
Room * current;
void checkInput(string);
string istypeof(string);
/*************User Defined Functions - Behind scenes**************/
void Add(string cond){
    string object_name, location;//location can be room/container
    int to_pos = cond.find(" to ");
    object_name = cond.substr(0, to_pos);
    location = cond.substr(to_pos + 4);
    int i;
    string object_type = istypeof(object_name);
    if(object_type == "Item"){
        for(i = 0; i < rooms.size(); i++){
            if(rooms[i]->name == location){
                rooms[i]->item.push_back(object_name);
                return;
            }
        }
        for(i = 0; i < containers.size(); i++){
            if(containers[i]->name == location){
                containers[i]->item.push_back(object_name);
                return;
            }
        }
    }
    if(object_type == "Creature"){
        for(i = 0; i < rooms.size(); i++){
            if(rooms[i]->name == location){
                rooms[i]->creature.push_back(object_name);
                return;
            }
        }
    }
    if(object_type == "Container"){
        for(i = 0; i < rooms.size(); i++){
            if(rooms[i]->name == location){
                rooms[i]->container.push_back(object_name);
                return;
            }
        }
    }
}
void Delete(string object_name){
    int i,j,k;
    for(i = 0; i < rooms.size(); i++){
        if(rooms[i]->name == object_name){
            for(j = 0; j < current -> border.size(); j++){
                if(current -> border[j] -> name == object_name){
                    current->border.erase(current->border.begin()+j);
                    return;
                }
            }
        }
    }
    for(i = 0; i < containers.size(); i++){
        if(containers[i]->name == object_name){
            for(j = 0; j < rooms.size(); j++){
                for(k = 0; k < rooms[j] -> container.size(); k++){
                    if(rooms[j] -> container[k] == object_name){
                        rooms[j] -> container.erase(rooms[j]->container.begin()+k);
                        return;
                    }
                }
            }
        }
    }
    for(i = 0; i < items.size(); i++){
        if(items[i]->name == object_name){
            for(j = 0; j < rooms.size(); j++){
                for(k = 0; k < rooms[j] -> item.size(); k++){
                    if(rooms[j] -> item[k] == object_name){
                        rooms[j] -> item.erase(rooms[j]->item.begin()+k);
                        return;
                    }
                }
            }
        }
    }
    for(i = 0; i < creatures.size(); i++){
        if(creatures[i]->name == object_name){
            for(j = 0; j < rooms.size(); j++){
                for(k = 0; k < rooms[j] -> creature.size(); k++){
                    if(rooms[j] -> creature[k] == object_name){
                        rooms[j] -> creature.erase(rooms[j]->creature.begin()+k);
                        return;
                    }
                }
            }
        }
    }
}
void Update(string cond){
    string object_name, new_status;
    int to_pos = cond.find(" to ");
    object_name = cond.substr(0, to_pos);
    new_status = cond.substr(to_pos + 4);
    int i;
    for(i = 0; i < rooms.size(); i++){
        if(rooms[i]->name == object_name){
            rooms[i]->status = new_status;
            return;
        }
    }
    for(i = 0; i < containers.size(); i++){
        if(containers[i]->name == object_name){
            containers[i]->status = new_status;
            return;
        }
    }
    for(i = 0; i < items.size(); i++){
        if(items[i]->name == object_name){
            items[i]->status = new_status;
            return;
        }
    }
    for(i = 0; i < creatures.size(); i++){
        if(creatures[i]->name == object_name){
            creatures[i]->status = new_status;
            return;
        }
    }
}
void GameOver(){
    END = true;
    cout << "Victory!" << endl;
}
void checkAction(string action){
    if(action.find("Add") != string::npos){
        Add(action.erase(0,4));
        return;
    }
    if(action.find("Delete") != string::npos){
        Delete(action.erase(0,7));
        return;
    }
    if(action.find("Update") != string::npos){
        Update(action.erase(0,7));
        return;
    }
    if(action == "Game Over"){
        GameOver();
        return;
    }
    checkInput(action);
}

/*************User Defined Functions - Handle inputs**************/
void changeRoom(string input){
    int i = (current -> border).size();
    int ind = 0;
    for(;ind < i; ind++){
        if(current -> border[ind] -> direction == input){
            string newroom = current -> border[ind] -> name;
            int j = rooms.size();
            for(int ind2 = 0; ind2 < j; ind2++){
                if(rooms[ind2] -> name == newroom){
                    current = rooms[ind2];
                }
            }
            break;
    }
  }
    if(ind == i){
        cout << "You can't go that way" << endl;
    }else{
        cout << current -> description << endl;
    }
}

void showInventory(){
	int i = inventory.size();
	if(i == 0){
		cout << "Inventory: Empty" << endl;
		return;
	}
	cout << "Inventory: ";
	int ind = 0;
	for(; ind < i - 1; ind++){
		cout << inventory[ind] << ",";
	}
	cout << inventory[ind] << endl;
}

void take(string input){
    int i,j,k;
	for(i = 0; i < (current -> item).size(); i++){
		if(current -> item[i] == input){
			inventory.push_back(input);
			(current -> item).erase((current -> item).begin() + i);
            cout << input << " has been added to inventory." << endl;
			return;
		}
	}
    
    for(i = 0; i < (current -> container).size(); i++){
        string container_name = current->container[i];
        for(j = 0; j < containers.size(); j++){
            if(containers[j]->name == container_name &&
               containers[j]->status == "unlocked"){
                for(k = 0; k < containers[j]->item.size();k++){
                    if(containers[j]->item[k] == input){
                        (current -> item).push_back(containers[j] -> item[k]);
                        containers[j] -> item.erase(containers[j]->item.begin()+k);
                        take(input);
                        return;
                    }
                }
            }
        }
    }
	cout << "There is no such thing to take." << endl;
}

void drop(string input){
    for(int i = 0; i < inventory.size(); i++){
        if(inventory[i] == input){
            (current -> item).push_back(input);
            inventory.erase(inventory.begin() + i);
            cout << input << " has been dropped." << endl;
            return;
        }
    }
    cout << "There is no such thing to drop." << endl;
}

void read(string input){
    for(int i = 0; i < inventory.size(); i++){
        if(inventory[i] == input){
            //Then I can read
            for(int j = 0; j < items.size(); j++){
                if(items[j] -> name == input){
                    if(items[j] -> writing.size() == 0){
                        cout<<"There's nothing to read." << endl;
                    }else{
                        cout << items[j] -> writing << endl;
                    }
                }
            }
            return;
        }
    }
    cout << "There is no such thing in your inventory to read." << endl;
}

void put(string a, string b){
    /*put a to b*/
    //check if a is in inventory
    int i = 0;
    for(; i < inventory.size(); i++){
        if(inventory[i] == a){
            //Then I can put
            //check if the current room is b
            int j = 0;
            if(current -> name == b){
                drop(a);
                return;
            }
            //if room is not b, check if the room has container b
            for(j = 0; j < current -> container.size(); j++){
                if(current -> container[j] == b){
                    break;
                }
            }
            if(j == current -> container.size()){
                cout<<"There is no such thing called "<<b<<" in this room."<<endl;
                return;
            }
            //room has container b, now access the container object
            for(j = 0; j < containers.size(); j++){
                if(containers[j] -> name == b){
                    //Check if it can accept a
                    if(containers[j]->accept.size() == 0){//not specified
                        containers[j] -> item.push_back(a);
                        inventory.erase(inventory.begin() + i);
                        cout << a << " has been put to "<<b<<endl;
                        if(containers[j]->status == ""){
                            containers[j]->status = "unlocked";
                        }
                        return;
                    }
                    for(int k = 0;k < containers[j]->accept.size();k++){
                        if(containers[j] -> accept[k] == a){
                            containers[j] -> item.push_back(a);
                            inventory.erase(inventory.begin() + i);
                            cout << a << " has been put to "<<b<<endl;
                            if(containers[j]->status == ""){
                                containers[j]->status = "unlocked";
                            }
                            return;
                        }
                    }
                    cout << b << " doesn't accept " << a << endl;
                    return;
                }
            }
            return;
        }
    }
    cout << "There is no such thing in your inventory. "<<endl;
}

void turnon(string input){
    /*turn on input*/
    //check if item is in inventory
    //check if the item can be turned on
    //turn on item
    for(int i = 0; i < inventory.size(); i++){
        if(inventory[i] == input){
            for(int j = 0; j < items.size(); j++){
                if(items[j] -> name == input){
                    if(items[j] -> turnonAble == false){
                        cout<<"Item is not able to be turned on." << endl;
                    }else{
                        cout<<"You activate the "<< items[j]->name<<"."<<endl;
                        cout << items[j] -> turnon.print << endl;
                        //Require action
                        checkAction(items[j] -> turnon.action);
                    }
                }
            }
            return;
        }
    }
    cout << "There is no such thing in your inventory to turn on." << endl;
}

void open(string input){
    int a;
    for(a = 0; a < current -> container.size(); a++){
        if(current -> container[a] == input){
            break;
        }
    }
    if(a == current -> container.size()){
        cout << "There is no such thing here to open." << endl;
        return;
    }
    for(int i = 0; i < containers.size(); i++){
        if(containers[i] -> name == input){
            //Then I can open
            if(containers[i] -> status == "locked"){
                cout << containers[i] -> name << " is locked." << endl;
                return;
            }
            if((containers[i] -> item).size() == 0){
                cout <<containers[i] -> name << " is empty." << endl;
                return;
            }
            containers[i] -> status = "unlocked";
            //Display message
            cout << containers[i] -> name << " contains ";
            int j;
            for(j = 0; j < (containers[i] -> item).size() - 1; j++){
                cout << containers[i] -> item[j] << ", ";
            }
            cout << containers[i] -> item[j] << endl;
            return;
        }
    }
}

void exit(){
    if(current -> type == "exit"){
        GameOver();
        return;
    }
    cout << "There is no exit in this room" << endl;
}

void attack(string monster, string weapon){
    //first, check if we have the monster in this room
    int i,j;
    for(i = 0; i < current -> creature.size(); i++){
        if(current -> creature[i] == monster){ break; }
    }
    //if yes, continue, if no, error message
    if(i == current -> creature.size()){
        cout<<"There is no such creature called "<<monster<<" here."<<endl;
        return;
    }
    //second, check if we have the weapon
    for(i = 0; i<inventory.size(); i++){
        if(inventory[i] == weapon){ break; }
    }
    //if yes, continue, if no, error message
    if(i == inventory.size()){
        cout<<"There is no such thing called "<<weapon<<" in your inventory."<<endl;
        return;
    }
    cout<<"You assault the "<<monster<<" with the "<<weapon<<endl;
    //third, check if weapon matches the vulnerability
    for(i = 0; i<creatures.size(); i++){
        if(creatures[i]->name == monster){ break;}
    }
    for(j = 0; j < creatures[i]->vulnerability.size();j++){
        if(creatures[i]->vulnerability[j] == weapon){ break;}
    }
    //if yes, continue, if no, error message
    if(j == creatures[i]->vulnerability.size()){
        cout<<"It seems "<<weapon<<" is useless to "<<monster<<endl;
        return;
    }
    //forth, check if we meet the condition
    if(creatures[i]->attack == NULL){
        return;
    }
    if(creatures[i]->attack->has_condition){
        //-object - can be room/container/creature/item
        string object = creatures[i]->attack->condition.object;
        //-status
        string status = creatures[i]->attack->condition.status;
        bool found = false;
        bool match = false;
        for(j = 0; j < items.size(); j++){
            if(items[j]->name == object){
                found = true;
                match = items[j]->status == status;
                break;
            }
        }
        if(!found){
            for(j = 0; j < rooms.size(); j++){
                if(rooms[j]->name == object){
                    found = true;
                    match = rooms[j]->status == status;
                    break;
                }
            }
        }
        if(!found){
            for(j = 0; j < containers.size(); j++){
                if(containers[j]->name == object){
                    found = true;
                    match = containers[j]->status == status;
                    break;
                }
            }
        }
        if(!found){
            for(j = 0; j < creatures.size(); j++){
                if(creatures[j]->name == object){
                    found = true;
                    match = creatures[j]->status == status;
                    break;
                }
            }
        }
        //if yes, continue, if no, error message
        if(!match){
            cout<<"You need make sure "<<object<<" is "<<status<<endl;
            return;
        }
    }
    //finally, print and take actions(plural)
    if(creatures[i]->attack->has_print){
        cout<<creatures[i]->attack->print<<endl;
    }
    if(creatures[i]->attack->has_action){
        for(j = 0; j<creatures[i]->attack->action.size();j++){
            checkAction(creatures[i]->attack->action[j]);
        }
    }
}

/********************User input manager***************************/
void checkInput(string input){
    if(input == "n" || input == "s" || input == "w" || input == "e"){
        changeRoom(input);
        return;
    }
    if(input == "i"){
        showInventory();
        return;
    }
    //take
    if(input == "take"){
        cout<<"What do you want to take? ";
        getline(cin, input);
        take(input);
        return;
    }
    if(input.find("take") != string::npos){
        input.erase(0,5);
        take(input);
        return;
    }
    //drop
    if(input == "drop"){
        cout<<"What do you want to drop? ";
        getline(cin, input);
        drop(input);
        return;
    }
    if(input.find("drop") != string::npos){
        input.erase(0,5);
        drop(input);
        return;
    }
    //read
    if(input == "read"){
        cout<<"What do you want to read? ";
        getline(cin, input);
        read(input);
        return;
    }
    if(input.find("read") != string::npos){
        input.erase(0,5);
        read(input);
        return;
    }
    //open
    if(input == "open"){
        cout<<"What do you want to open? ";
        getline(cin, input);
        if(input == "exit"){
            exit();
            return;
        }
        open(input);
        return;
    }
    if(input.find("open") != string::npos){
        input.erase(0,5);
        if(input == "exit"){
            exit();
            return;
        }
        open(input);
        return;
    }
    //put
    if(input == "put"){
        cout << "What do you want to put? ";
        getline(cin, input);
        string put_item = input;
        cout << "Where do you want to put in? ";
        getline(cin, input);
        string put_into = input;
        put(put_item, put_into);
        return;
    }
    if(input.find("put") != string::npos){
        input.erase(0,4);
        int space_pos = input.find(" "); //it's also size of item string
        string put_item = input.substr(0, space_pos);
        input.erase(0, space_pos + 4);
        string put_into = input;
        put(put_item, put_into);
        return;
    }
    //turn on
    if(input == "turn on"){
        cout<<"What do you want to turn on? ";
        getline(cin, input);
        turnon(input);
        return;
    }
    if(input.find("turn on") != string::npos){
        input.erase(0,8);
        turnon(input);
        return;
    }
    //attack
    if(input == "attack"){
        cout << "What do you want to attack? ";
        getline(cin, input);
        string monster = input;
        cout << "What do you want to attack with? ";
        getline(cin, input);
        string weapon = input;
        attack(monster, weapon);
        return;
    }
    if(input.find("attack") != string::npos){
        input.erase(0,7);
        int space_pos = input.find(" "); //it's also size of item string
        string monster = input.substr(0, space_pos);
        input.erase(0, space_pos + 6);
        string weapon = input;
        attack(monster, weapon);
        return;
    }
  cout << "Error" << endl;
}

/***********************For my triggers***************************/
string istypeof(string o){
    int i;
    for(i = 0; i < rooms.size(); i++){
        if(rooms[i] -> name == o){
            return "Room";
        }
    }
    for(i = 0; i < items.size(); i++){
        if(items[i] -> name == o){
            return "Item";
        }
    }
    for(i = 0; i < containers.size(); i++){
        if(containers[i] -> name == o){
            return "Container";
        }
    }
    for(i = 0; i < creatures.size(); i++){
        if(creatures[i] -> name == o){
            return "Creature";
        }
    }
    return NULL;
}
bool ownerTrigger(Trigger * t){//3 conditions
    //check owner, go to it(inventory/room/container...)
    string owner = t -> owner.owner;
    //check has, (yes/no)
    string has = t -> owner.has;
    //check object,
    string object = t -> owner.object;
        //if owner + object == true && has == yes,
        // or owner + object == false && has == no, trigger activated
            //print and actions(plural), return true
    int i,j,k;
    if(owner == "inventory"){
        for(i = 0; i < inventory.size(); i++){
            if(inventory[i] == object){
                if(has == "yes"){
                    if(t->has_print){cout<<t->print<<endl;}
                    if(t->has_action){
                        for(k = 0; k < t->action.size();k++){
                            checkAction(t->action[k]);
                        }
                    }
                    return true;
                }else{return false;}
            }
        }
        if(i == inventory.size()){
            if(has == "no"){
                if(t->has_print){cout<<t->print<<endl;}
                if(t->has_action){
                    for(k = 0; k < t->action.size();k++){
                        checkAction(t->action[k]);
                    }
                }
                return true;
            }else{return false;}
        }
    }
    string owner_type = istypeof(owner);
    string object_type = istypeof(object);
    if(owner_type == "Room"){
        for(i = 0; i < rooms.size(); i++){
            if(rooms[i] -> name == owner){
                if(object_type == "Item"){
                    for(j = 0; j < rooms[i] -> item.size(); j++){
                        if(rooms[i]->item[j] == object){
                            if(has == "yes"){
                                if(t->has_print){cout<<t->print<<endl;}
                                if(t->has_action){
                                    for(k = 0; k < t->action.size();k++){
                                        checkAction(t->action[k]);
                                    }
                                }
                                return true;
                            }else{return false;}
                        }
                    }
                    if(j == rooms[i]->item.size()){
                        if(has == "no"){
                            if(t->has_print){cout<<t->print<<endl;}
                            if(t->has_action){
                                for(k = 0; k < t->action.size();k++){
                                    checkAction(t->action[k]);
                                }
                            }
                            return true;
                        }else{return false;}
                    }
                }
                if(object_type == "Container"){
                    for(j = 0; j < rooms[i] -> container.size(); j++){
                        if(rooms[i]->container[j]== object ){
                            if(has == "yes"){
                                if(t->has_print){cout<<t->print<<endl;}
                                if(t->has_action){
                                    for(k = 0; k < t->action.size();k++){
                                        checkAction(t->action[k]);
                                    }
                                }
                                return true;
                            }else{return false;}
                        }
                    }
                    if(j == rooms[i]->container.size()){
                        if(has == "no"){
                            if(t->has_print){cout<<t->print<<endl;}
                            if(t->has_action){
                                for(k = 0; k < t->action.size();k++){
                                    checkAction(t->action[k]);
                                }
                            }
                            return true;
                        }else{return false;}
                    }
                }
                if(object_type == "Creature"){
                    for(j = 0; j < rooms[i] -> creature.size(); j++){
                        if(rooms[i]->creature[j]== object){
                            if(has == "yes"){
                                if(t->has_print){cout<<t->print<<endl;}
                                if(t->has_action){
                                    for(k = 0; k < t->action.size();k++){
                                        checkAction(t->action[k]);
                                    }
                                }
                                return true;
                            }else{return false;}
                        }
                    }
                    if(j == rooms[i]->creature.size()){
                        if(has == "no"){
                            if(t->has_print){cout<<t->print<<endl;}
                            if(t->has_action){
                                for(k = 0; k < t->action.size();k++){
                                    checkAction(t->action[k]);
                                }
                            }
                            return true;
                        }else{return false;}
                    }
                }
            }
        }
    }
    if(owner_type == "Container"){
        for(i = 0; i < containers.size(); i++){
            if(containers[i]->name == owner){
                //Object can only be item
                for(j = 0; j < containers[i] -> item.size(); j++){
                    if(containers[i]->item[j]== object){
                        if(has == "yes"){
                            if(t->has_print){cout<<t->print<<endl;}
                            if(t->has_action){
                                for(k = 0; k < t->action.size();k++){
                                    checkAction(t->action[k]);
                                }
                            }
                            return true;
                        }else{return false;}
                    }
                }
                if(j == containers[i]->item.size()){
                    if(has == "no"){
                        if(t->has_print){cout<<t->print<<endl;}
                        if(t->has_action){
                            for(k = 0; k < t->action.size();k++){
                                checkAction(t->action[k]);
                            }
                        }
                        return true;
                    }else{return false;}
                }
            }
        }
    }
    return false;
}
bool statusTrigger(Trigger * t){//2 conditions
    //check object
    string object = t -> status.object;
    //check object's status
    string status = t -> status.status;
        //if it matches the status
        //print and actions(plural), return true
    int i,j;
    string object_type = istypeof(object);
    if(object_type == "Room"){
        for(i = 0; i < rooms.size(); i++){
            if(rooms[i] -> name == object){
                if(rooms[i] -> status == status){
                    if(t->has_print){cout<<t->print<<endl;}
                    if(t->has_action){
                        for(j = 0; j < t->action.size();j++){
                            checkAction(t->action[j]);
                        }
                    }
                    return true;
                }
            }
        }
    }
    if(object_type == "Item"){
        for(i = 0; i < items.size(); i++){
            if(items[i] -> name == object){
                if(items[i] -> status == status){
                    if(t->has_print){cout<<t->print<<endl;}
                    if(t->has_action){
                        for(j = 0; j < t->action.size();j++){
                            checkAction(t->action[j]);
                        }
                    }
                    return true;
                }
            }
        }
    }
    if(object_type == "Container"){
        for(i = 0; i < containers.size(); i++){
            if(containers[i] -> name == object){
                if(containers[i] -> status == status){
                    if(t->has_print){cout<<t->print<<endl;}
                    if(t->has_action){
                        for(j = 0; j < t->action.size();j++){
                            checkAction(t->action[j]);
                        }
                    }
                    return true;
                }
            }
        }
    }
    if(object_type == "Creature"){
        for(i = 0; i < creatures.size(); i++){
            if(creatures[i] -> name == object){
                if(creatures[i] -> status == status){
                    if(t->has_print){cout<<t->print<<endl;}
                    if(t->has_action){
                        for(j = 0; j < t->action.size();j++){
                            checkAction(t->action[j]);
                        }
                    }
                    return true;
                }
            }
        }
    }
    return false;
}
bool checkTrigger_nocommand(){
    //check all triggers and pick the triggers with no command
    //if it is permanent, continue, no worry
    //else if it is single, check if it has not been executed before
    //if it is 3-cond, go to ownerTrigger()
    //else if it is 2-cond, go to statusTrigger()
    int i,j,k;
    bool rresult = false;
    bool iresult = false;
    bool cresult = false;
    bool crresult = false;
    Trigger *t;
    //current room trigger
    if(current -> trigger.size() != 0){ //has triggers~
        for(j = 0; j < current->trigger.size(); j++){
            t = current->trigger[j];
            if(!t->has_command){
                if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                    if(t->condition == 3){rresult = ownerTrigger(t);}
                    else if(t->condition == 2){rresult = statusTrigger(t);}
                    if(rresult == true){t->times++;}
                }
            }
        }
    }
    //current room's item trigger
    for(i = 0; i < current -> item.size(); i++){
        string target = current -> item[i];
        for(j = 0; j < items.size(); j++){
            if(items[j] -> name == target){
                for(k = 0; k < items[j] -> trigger.size(); k++){
                    t = items[j] -> trigger[k];
                    if(!t->has_command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){iresult = ownerTrigger(t);}
                            else if(t->condition == 2){iresult = statusTrigger(t);}
                            if(iresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    //current room's container trigger
    for(i = 0; i < current -> container.size(); i++){
        string target = current -> container[i];
        for(j = 0; j < containers.size(); j++){
            if(containers[j] -> name == target){
                for(k = 0; k < containers[j] -> trigger.size(); k++){
                    t = containers[j] -> trigger[k];
                    if(!t->has_command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){cresult = ownerTrigger(t);}
                            else if(t->condition == 2){cresult = statusTrigger(t);}
                            if(cresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    //current room's creature trigger
    for(i = 0; i < current -> creature.size(); i++){
        string target = current -> creature[i];
        for(j = 0; j < creatures.size(); j++){
            if(creatures[j] -> name == target){
                for(k = 0; k < creatures[j] -> trigger.size(); k++){
                    t = creatures[j] -> trigger[k];
                    if(!t->has_command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){crresult = ownerTrigger(t);}
                            else if(t->condition == 2){crresult = statusTrigger(t);}
                            if(crresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    return (rresult||iresult||cresult||crresult);
}
bool checkTrigger_withcommand(string input){
    //truncate input
    //check all triggers and pick the triggers with command
    //if it is permanent, continue, no worry
    //else if it is single, check if it has been executed before
    //if it is 3-cond, go to ownerTrigger()
    //else if it is 2-cond, go to statusTrigger()
    int i,j,k;
    bool rresult = false;
    bool iresult = false;
    bool cresult = false;
    bool crresult = false;
    Trigger *t;
    //current room trigger
    if(current -> trigger.size() != 0){ //has triggers~
        for(j = 0; j < current->trigger.size(); j++){
            t = current->trigger[j];
            if(t -> has_command && input == t -> command){
                if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                    if(t->condition == 3){rresult = ownerTrigger(t);}
                    else if(t->condition == 2){rresult = statusTrigger(t);}
                    if(rresult == true){t->times++;}
                }
            }
        }
    }
    //inventory's item trigger
    for(i = 0; i < inventory.size(); i++){
        string target = inventory[i];
        for(j = 0; j < items.size(); j++){
            if(items[j] -> name == target){
                for(k = 0; k < items[j] -> trigger.size(); k++){
                    t = items[j] -> trigger[k];
                    if(t -> has_command && input == t -> command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){iresult = ownerTrigger(t);}
                            else if(t->condition == 2){iresult = statusTrigger(t);}
                            if(iresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    //current room's container trigger
    for(i = 0; i < current -> container.size(); i++){
        string target = current -> container[i];
        for(j = 0; j < containers.size(); j++){
            if(containers[j] -> name == target){
                for(k = 0; k < containers[j] -> trigger.size(); k++){
                    t = containers[j] -> trigger[k];
                    if(t -> has_command && input == t -> command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){cresult = ownerTrigger(t);}
                            else if(t->condition == 2){cresult = statusTrigger(t);}
                            if(cresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    //current room's creature trigger
    for(i = 0; i < current -> creature.size(); i++){
        string target = current -> creature[i];
        for(j = 0; j < creatures.size(); j++){
            if(creatures[j] -> name == target){
                for(k = 0; k < creatures[j] -> trigger.size(); k++){
                    t = creatures[j] -> trigger[k];
                    if(t -> has_command && input == t -> command){
                        if(t->type == "permanent" || (t->type == "single" && t->times == 0)){
                            if(t->condition == 3){crresult = ownerTrigger(t);}
                            else if(t->condition == 2){crresult = statusTrigger(t);}
                            if(crresult == true){t->times++;}
                        }
                    }
                }
            }
        }
    }
    return (rresult||iresult||cresult||crresult);
}
#endif
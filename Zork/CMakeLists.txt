cmake_minimum_required(VERSION 3.6)
project(Zork)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    rapidxml-1.13/rapidxml.hpp
    rapidxml-1.13/rapidxml_iterators.hpp
    rapidxml-1.13/rapidxml_print.hpp
    rapidxml-1.13/rapidxml_utils.hpp
    samples/containersample.out
    samples/containersample.txt
    samples/containersample.xml
    samples/creaturesample.out
    samples/creaturesample.txt
    samples/creaturesample.xml
    samples/itemsample.out
    samples/itemsample.txt
    samples/itemsample.xml
    samples/README
    samples/roomsample.out
    samples/roomsample.txt
    samples/roomsample.xml
    samples/triggersample.out
    samples/triggersample.txt
    samples/triggersample.xml
    Attack.hpp
    Container.hpp
    Creature.hpp
    header.hpp
    Item.hpp
    main.cpp
    Room.hpp
    Trigger.hpp)

add_executable(Zork ${SOURCE_FILES})
Devoxx France 2024 Amber Lab
============================

## Introduction

This lab covers three elements of the Amber project:

1. Text blocks
2. Records
3. Sealed types and pattern matching

Each part is implemented in its own module, so that you can follow one or the other, independently. 

The fourth part is about using Data Oriented Programming to refactor a small application. This part of the lab takes you through an  example where you can refactor a small application using sealed types, records, and pattern matching. The application you will have to refactor is currently written using an object-oriented approach. And it is not quite right: the core business module depends on its implementation details, namely the database and an external service. 

After your refactoring, these modules will depend on your core business module, and you will be able to enrich your object model using sealed types and records. 


## Working on the Lab

Each module contains its own Readme.md file, with the instructions and hints on how to work on the different parts of the lab. They are all independent, so you can choose the one you want to work on. 

## References

- JEP 378 Text Blocks: https://openjdk.org/jeps/378
- JEP 361 Switch Expressions: https://openjdk.org/jeps/361
- JEP 395 Records: https://openjdk.org/jeps/395
- JEP 395 Pattern Matching for instanceof: https://openjdk.org/jeps/394
- JEP 440 Record Patterns: https://openjdk.org/jeps/440
- JEP 441 Pattern Matching for switch: https://openjdk.org/jeps/441
- JEP 456 Unnamed Variables & Patterns: https://openjdk.org/jeps/456

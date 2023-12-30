# ID3 Decision Tree Algorithm Implementation in Java

## Overview
This repository contains a Java implementation of the ID3 (Iterative Dichotomiser 3) algorithm, a popular method used in machine learning for creating decision trees. The implementation is designed to build a decision tree based on a dataset for classification purposes.

## Features
- **Tree Construction**: Constructs a decision tree from a given dataset.
- **Entropy Calculation**: Computes entropy to measure the purity of a subset.
- **Information Gain**: Uses information gain to select the best attribute for node splitting.
- **Tree Pruning**: Optional functionality to prevent overfitting by pruning the tree.
- **Prediction & Accuracy**: Utilizes the constructed tree to make predictions and calculates the accuracy based on test data.

## Class Structure
- `Node`: Represents a node in the decision tree with attributes like `state`, `atribut`, `parent`, `childNode`, and `children`.
- `Id3`: Main class for the ID3 algorithm implementation, containing methods for tree construction, prediction, and accuracy calculation.

## Usage
To use this implementation, provide a dataset in a CSV format. The dataset should be split into training and test data. The program will build a decision tree based on the training data and then use the tree to make predictions on the test data.

## Example
A simple example of using this implementation:

```java
Id3 id3Algorithm = new Id3();
id3Algorithm.readCSV("path/to/training/data.csv", "path/to/test/data.csv");
id3Algorithm.id3();
System.out.println("Decision Tree Constructed.");
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
```
## Output for volleyball_test.csv
```java
IG(weather) 0.2467498197744391
IG(temperature) 0.029222565658954647
IG(humidity) 0.15183550136234136
IG(wind) 0.04812703040826927
weather
IG(weather) 0.0
IG(temperature) 0.01997309402197489
IG(humidity) 0.01997309402197489
IG(wind) 0.9709505944546686
wind
IG(weather) 0.0
IG(temperature) 0.5709505944546686
IG(humidity) 0.9709505944546686
IG(wind) 0.01997309402197489
humidity
null 0
cloudy 1
rainy 1
sunny 1
strong 2
weak 2
normal 2
high 2
[BRANCHES]:
1:weather=cloudy yes
1:weather=rainy 2:wind=strong no
1:weather=rainy 2:wind=weak yes
1:weather=sunny 2:humidity=normal yes
1:weather=sunny 2:humidity=high no
[PREDICTIONS]: yes yes yes yes no yes yes yes no yes yes no yes no no yes yes yes yes 
[ACCURACY]: 0.57895
[CONFUSION_MATRIX]:
4 7
1 7

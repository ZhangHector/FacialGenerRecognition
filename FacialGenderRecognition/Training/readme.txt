To train the model:
	1. Downloaded the training data set from https://www.openu.ac.il/home/hassner/Adience/data.html. Extract and put the aligned directory and the 5_folds to the current directory.
	2. navigate to project rood direcotry.
	3. run python preprocess_data.py --fold 5
	4. run python train.py --validation_fold 0
	5. To plot the training history, run python plot_history.py -i ./history.h5
	6. To run the real-time demo: python demo.py
FacialGenerRecognition
===================================

Facial gender recognition has been relevant to a lot of applications. Since recent
advances in convolutional neural network (CNN), a significant increase in
performance can be obtained on this task.

Introduction
------------

In this project, we will explore how to build
a CNN for gender classification as well as evaluate the performance of our model. At
the end, an android mobile app will be developed to demo our trained gender
classification model.

Extracting information from human facial image can be useful in various scenarios.
For instance, a website can automate part of the new user signing up process by
taking user’s facial image using webcam. The traditional face recognition algorithms
rely on facial feature models. Different model need to be built for different
classification problems. The advantage of using CNN for image classification is that
it uses relatively little pre-processing compared to other image classification
algorithms. This means that the network learns the filters that in traditional algorithms
were hand-engineered. Therefore, a lot of human efforts can be saved by using
this approach.

CNN Model Architecture
----------------------
<img src="Report/Architecture of CNN.png" height="600" alt="Screenshot"/> 

Experiment
----------

Our method is implemented using Keras with Tensorflow backend. Our model is
trained by 20 epochs. The final accuracy rate we get is 80.08%. The following graph
shows training history of our model.
<img src="Report/Figures/Accuracy.png" height="600" alt="Screenshot"/>
<img src="Report/Figures/Loss.png" height="600" alt="Screenshot"/>

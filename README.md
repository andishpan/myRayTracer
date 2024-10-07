Overview

This project is a 3D Ray Tracing engine built in Java. Ray tracing is a rendering technique that simulates the way light interacts with objects in a scene to generate realistic images. 
I have  implemented various geometric shapes (spheres, cubes, cylinders, etc.) and rendering techniques to create a scene with lighting, shadows, reflections, and other visual effects.



Features

  - Supports rendering of basic 3D objects such as spheres, cubes, planes, and cylinders.
 -  Implements Constructive Solid Geometry (CSG) for operations such as union, intersection, and difference on objects.
 -  Includes different light sources and customizable camera positioning.
 -  Supports reflection and basic texture mapping.
 -  Utilizes bounding volume hierarchies (BVH) to optimize rendering performance.
 -  Includes a skydome for realistic environment rendering.
 -  Supports 2x2 and 4x4 super sampling

  Key Classes

    RayTracer.java: The main class that drives the ray tracing process.
    Ray.java: Represents a ray in 3D space.
    Scene.java: Manages the collection of objects and lights in the scene.
    Light.java: Defines light sources in the scene.
    Sphere.java, Cube.java, Plane.java, etc.: Defines the geometric objects that can be rendered.
    CSGUnion.java, CSGIntersection.java, CSGDifference.java: Implement Constructive Solid Geometry (CSG) operations.

Prerequisites

    Java 11 or later
    Maven for building the project

Build and Run

    Clone the repository (if using version control):

    bash

    git clone <repository-url>
    cd myRayTracer

Build the project using Maven:

    bash

    mvn clean install

Run the Ray Tracer: After building, you can run the RayTracer main class to start rendering a scene:

    bash

    java -cp target/myRayTracer-1.0-SNAPSHOT.jar com.raytracer.RayTracer

Customizing the Scene

To customize the scene, modify the Scene.java file, where you can add objects, set their positions, apply textures, and configure lighting.

Example:

    Scene scene = new Scene();
    scene.addObject(new Sphere(new Vector3f(0, 0, -5), 1));  // Adds a sphere at (0, 0, -5)
    scene.addLight(new Light(new Vector3f(10, 10, 10), new Color(1, 1, 1)));  // Adds a light source


Future Improvements

   - Support for more advanced textures and materials.
   - Optimization techniques such as adaptive sampling.
   - Implementation of global illumination techniques.

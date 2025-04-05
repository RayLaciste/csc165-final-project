package a3;

import tage.*;
import tage.shapes.*;
import org.joml.*;

public class ManualObj extends ManualObject {
    private Vector3f[] vertices = new Vector3f[4];
    private Vector2f[] texcoords = new Vector2f[4];
    private Vector3f[] normals = new Vector3f[4];
    private int[] indices = new int[] { 0, 1, 2,
                                        1, 2, 3
    };

    public ManualObj() {
        super();

        vertices[0] = (new Vector3f()).set(-1.0f, 0.0f, -0.25f);
        vertices[1] = (new Vector3f()).set(0.0f, 0.0f, 1.0f);
        vertices[2] = (new Vector3f()).set(0.0f, 0f, 0.0f);
        vertices[3] = (new Vector3f()).set(1.0f, 0.0f, -0.25f);

        texcoords[0] = (new Vector2f()).set(0f, 0f);
        texcoords[1] = (new Vector2f()).set(.5f, 1f);
        texcoords[2] = (new Vector2f()).set(.5f, 0f);
        texcoords[3] = (new Vector2f()).set(1f, 0f);

        normals[0] = (new Vector3f()).set(0f, 0f, 1f);
        normals[1] = (new Vector3f()).set(0f, 0f, 1f);
        normals[2] = (new Vector3f()).set(0f, 0f, 1f);
        normals[3] = (new Vector3f()).set(0f, 0f, 1f);

        // there are 5 indexed vertices, but the object has 6 vertices
        setNumVertices(6);
        setVerticesIndexed(indices, vertices);
        setTexCoordsIndexed(indices, texcoords);
        setNormalsIndexed(indices, normals);
        setMatAmb(Utils.goldAmbient());
        setMatDif(Utils.goldDiffuse());
        setMatSpe(Utils.goldSpecular());
        setMatShi(Utils.goldShininess());
    }
}

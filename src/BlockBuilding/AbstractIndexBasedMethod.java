/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    Copyright (C) 2015 George Antony Papadakis (gpapadis@yahoo.gr)
 */
package BlockBuilding;

import DataStructures.AbstractBlock;
import DataStructures.Attribute;
import DataStructures.EntityProfile;
import Utilities.Constants;
import Utilities.ExportBlocks;
import Utilities.SerializationUtilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author gap2
 */
public abstract class AbstractIndexBasedMethod extends AbstractBlockingMethod implements Constants {

    protected final boolean cleanCleanER;
    protected int sourceId;
    protected double[] noOfEntities;

    protected List<AbstractBlock> blocks;
    protected final String[] entitiesPath;
    protected final String[] indexPath;
    protected Directory[] indexDirectory;
    protected final List<EntityProfile>[] entityProfiles;
            
    public AbstractIndexBasedMethod(String description, List<EntityProfile>[] profiles) {
        super(description);
        entitiesPath = null;
        indexPath = null;
        blocks = new ArrayList<>();
        entityProfiles = profiles;
        noOfEntities = new double[entityProfiles.length];
        if (entityProfiles.length == 2) {
            cleanCleanER = true;
        } else {
            cleanCleanER = false;
        }
    }

    public AbstractIndexBasedMethod(String description, String[] entities, String[] index) {
        super(description);
        entitiesPath = entities;
        indexPath = index;
        blocks = new ArrayList<>();
        entityProfiles = new List[entitiesPath.length];
        noOfEntities = new double[entitiesPath.length];
        if (entitiesPath.length == 2) {
            cleanCleanER = true;
        } else {
            cleanCleanER = false;
        }
    }

    @Override
    public List<AbstractBlock> buildBlocks() {
        setDirectory();
        
        //create Lucene index on disk
        sourceId = 0; // used by Attribute Clustering, as well, that's why it's not an argument
        buildIndex();
        if (cleanCleanER) {
            sourceId = 1;
            buildIndex();
        }

        //extract blocks from Lucene index
        ExportBlocks exportBlocks = new ExportBlocks(indexDirectory);
        return exportBlocks.getBlocks();
    }

    protected void buildIndex() {
        List<EntityProfile> entityProfiles = getProfiles();
        IndexWriter iWriter = openWriter(indexDirectory[sourceId]);
        indexEntities(iWriter, entityProfiles);
        closeWriter(iWriter);
        noOfEntities[sourceId] = entityProfiles.size();
    }

    protected void closeWriter(IndexWriter iWriter) {
        try {
            iWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected abstract Set<String> getBlockingKeys(String attributeValue);

    public double getBruteForceComparisons() {
        if (noOfEntities.length == 1) {
            return noOfEntities[0] * (noOfEntities[0] - 1) / 2;
        }
        return noOfEntities[0] * noOfEntities[1];
    }

    protected List<EntityProfile> getProfiles() {
        if (entitiesPath != null) {
            entityProfiles[sourceId] = loadEntities(entitiesPath[sourceId]);
        }
        return entityProfiles[sourceId];
    }
    
    public double getTotalNoOfEntities() {
        if (noOfEntities.length == 1) {
            return noOfEntities[0];
        }
        return noOfEntities[0] + noOfEntities[1];
    }

    protected void indexEntities(IndexWriter index, List<EntityProfile> entities) {
        try {
            int counter = 0;
            for (EntityProfile profile : entities) {
                Document doc = new Document();
                doc.add(new StoredField(DOC_ID, counter++));
                for (Attribute attribute : profile.getAttributes()) {
                    getBlockingKeys(attribute.getValue()).stream().filter((key) -> (0 < key.trim().length())).forEach((key) -> {
                        doc.add(new StringField(VALUE_LABEL, key.trim(), Field.Store.YES));
                    });
                }
                index.addDocument(doc);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected List<EntityProfile> loadEntities(String entitiesPath) {
        return (List<EntityProfile>) SerializationUtilities.loadSerializedObject(entitiesPath);
    }

    protected IndexWriter openWriter(Directory directory) {
        try {
            Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_40);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
            return new IndexWriter(directory, config);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected abstract void setDirectory();

    protected void setDiskDirectory() {
        indexDirectory = ExportBlocks.getDirectories(indexPath);
    }

    protected void setMemoryDirectory() {
        indexDirectory = new Directory[entityProfiles.length];
        for (int i = 0; i < entityProfiles.length; i++) {
            indexDirectory[i] = new RAMDirectory();
        }
    }
}

package org.alfresco.test.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.test.util.CMISUtil.DocumentAspect;
import org.alfresco.test.util.CMISUtil.DocumentType;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test content aspects crud operations.
 * 
 * @author Bogdan Bocancea
 */

public class ContentAspectsTests extends AbstractTest
{
    UserService userService;
    SiteService site;
    ContentService content;
    ContentAspects contentAspect;
    String admin = "admin";
    String password = "password";
    private final String DATE_FORMAT = "EEE MMM dd HH:mm";
    
    @BeforeClass
    public void setup()
    {
        userService = (UserService) ctx.getBean("userService");
        site = (SiteService) ctx.getBean("siteService");
        content = (ContentService) ctx.getBean("contentService");
        contentAspect = (ContentAspects) ctx.getBean("contentAspects");
    }
    
    @Test
    public void addDocAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addAspect(userName, password, siteName, plainDoc, DocumentAspect.DUBLIN_CORE);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);       
        Assert.assertTrue(properties.toString().contains(DocumentAspect.DUBLIN_CORE.getProperty()));
    }
    
    @Test(expectedExceptions = CmisRuntimeException.class)
    public void addAspectInvalidDoc() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        contentAspect.addAspect(userName, password, siteName, plainDoc, DocumentAspect.DUBLIN_CORE);
    }
    
    @Test
    public void addDocComplianceableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date removeAfter = calendar.getTime();
        contentAspect.addComplianceable(userName, password, siteName, plainDoc, removeAfter); 
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertTrue(contentAspect.getPropertyValue(properties, "cm:removeAfter").contains(new SimpleDateFormat(DATE_FORMAT).format(removeAfter)));
    }
    
    @Test 
    public void addFolderComplianceableAspect() throws Exception
    {
        String siteName = "siteCMIS-" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String folder = "cmisFolder"; 
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        content.createFolder(userName, password, folder, siteName);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date removeAfter = calendar.getTime();
        contentAspect.addComplianceable(userName, password, siteName, folder, removeAfter);   
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, folder);
        Assert.assertTrue(contentAspect.getPropertyValue(properties, "cm:removeAfter").contains(new SimpleDateFormat(DATE_FORMAT).format(removeAfter)));
    }
    
    @Test
    public void removeDocComplianceable() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date removeAfter = calendar.getTime();
        contentAspect.addComplianceable(userName, password, siteName, plainDoc, removeAfter);    
        contentAspect.removeAspect(userName, password, siteName, plainDoc, DocumentAspect.COMPLIANCEABLE);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:removeAfter"), null);
    }
    
    @Test 
    public void removeFolderComplianceable() throws Exception
    {
        String siteName = "siteCMIS-" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String folder = "cmisFolder"; 
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        content.createFolder(userName, password, folder, siteName);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date removeAfter = calendar.getTime();
        contentAspect.addComplianceable(userName, password, siteName, folder, removeAfter); 
        contentAspect.removeAspect(userName, password, siteName, folder, DocumentAspect.COMPLIANCEABLE);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, folder);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "removeAfter"), null);
    }
    
    @Test(expectedExceptions = CmisRuntimeException.class)
    public void removeAspectInvalidContent() throws Exception
    {
        String siteName = "siteCMIS-" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String folder = "cmisFolder"; 
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        contentAspect.removeAspect(userName, password, siteName, folder, DocumentAspect.COMPLIANCEABLE);
    }
    
    @Test
    public void addDocDublinCore() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());        
        contentAspect.addDublinCore(userName, password, siteName, plainDoc, userName, userName, "Plain text", userName, 
                plainDoc, "90", "Best Rights", plainDoc);    
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:contributor"), userName);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:publisher"), userName);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:type"), "Plain text");
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:identifier"), userName);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:dcsource"), plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:coverage"), "90");
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:rights"), "Best Rights");
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:subject"), plainDoc);
    }
    
    @Test
    public void removeDublinCore() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());        
        contentAspect.addDublinCore(userName, password, siteName, plainDoc, userName, userName, "Plain text", userName, 
                plainDoc, "90", "Best Rights", plainDoc);     
        contentAspect.removeAspect(userName, password, siteName, plainDoc, DocumentAspect.DUBLIN_CORE);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, DocumentAspect.DUBLIN_CORE.getProperty()), null);
    }
    
    @Test
    public void addEffectivity() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date toDate = calendar.getTime();
        Date fromDate = new Date();
        contentAspect.addEffectivity(userName, password, siteName, plainDoc, fromDate, toDate);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);  
        Assert.assertTrue(contentAspect.getPropertyValue(properties, "cm:from").contains(new SimpleDateFormat(DATE_FORMAT).format(fromDate)));
        Assert.assertTrue(contentAspect.getPropertyValue(properties, "cm:to").contains(new SimpleDateFormat(DATE_FORMAT).format(toDate)));
    }
    
    @Test
    public void addGeographicAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Double longitude = 46.803138;
        Double latitude = 26.614806;
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addGeographicAspect(userName, password, siteName, plainDoc, longitude, latitude);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:longitude"), longitude.toString());
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:latitude"), latitude.toString());
    }
    
    @Test
    public void addSummarizableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        String summary = "Test summary";
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addSummarizable(userName, password, siteName, plainDoc, summary);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:summary"), summary);
    }
    
    @Test
    public void addTemplatableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        String templateDoc = "template" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Document template = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, templateDoc, templateDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addTemplatable(userName, password, siteName, plainDoc, templateDoc);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:template"), template.getId().split(";")[0]);
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void addTemplatableFakeTemplate() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addTemplatable(userName, password, siteName, plainDoc, "fakeTemplate");
    }
    
    @Test
    public void removeTemplatableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        String templateDoc = "template" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, templateDoc, templateDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addTemplatable(userName, password, siteName, plainDoc, templateDoc);
        contentAspect.removeAspect(userName, password, siteName, plainDoc, DocumentAspect.TEMPLATABLE);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:template"), null);
    }
    
    @Test
    public void addEmailedAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        String addressee = "testUser@test.com";
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        Date sentDate = new Date();
        List<String> addressees = new ArrayList<String>();
        addressees.add("test2@test.com");
        addressees.add("test3@test.com");
        contentAspect.addEmailed(userName, password, siteName, plainDoc, addressee, addressees, "Subject", userName, sentDate);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:addressee"), addressee);
        Assert.assertEquals(contentAspect.getValues(properties, "cm:addressees"), addressees);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:subjectline"), "Subject");
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:originator"), userName);
        Assert.assertTrue(contentAspect.getPropertyValue(properties, "cm:sentdate").contains(new SimpleDateFormat(DATE_FORMAT).format(sentDate)));       
    }
    
    @Test
    public void addIndexControlAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addIndexControl(userName, password, siteName, plainDoc, true, true);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:isIndexed"), "true");
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "cm:isContentIndexed"), "true");
    }
    
    @Test
    public void addRestrictableAspectAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        int hours = 5;
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addRestrictable(userName, password, siteName, plainDoc, hours);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);   
        Assert.assertEquals(contentAspect.getPropertyValue(properties, "dp:offlineExpiresAfter"), String.valueOf(TimeUnit.HOURS.toMillis(hours)));
    }
    
    @Test
    public void addClasifiableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        List<String> categories = new ArrayList<String>();
        categories.add("Regions");
        categories.add("Languages");
        contentAspect.addClasifiable(userName, password, siteName, plainDoc, categories);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        List<?> values = contentAspect.getValues(properties, "cm:categories");
        Assert.assertEquals(values.get(0), contentAspect.getCategoryNodeRef(userName, password, categories.get(0)));
        Assert.assertEquals(values.get(1), contentAspect.getCategoryNodeRef(userName, password, categories.get(1)));
    }
    
    @Test
    public void removeClasifiableAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "doc" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);        
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        List<String> categories = new ArrayList<String>();
        categories.add("Regions");
        categories.add("Languages");
        contentAspect.addClasifiable(userName, password, siteName, plainDoc, categories);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        List<?> values = contentAspect.getValues(properties, "cm:categories");
        Assert.assertEquals(values.get(0), contentAspect.getCategoryNodeRef(userName, password, categories.get(0)));
        Assert.assertEquals(values.get(1), contentAspect.getCategoryNodeRef(userName, password, categories.get(1)));      
        contentAspect.removeAspect(userName, password, siteName, plainDoc, DocumentAspect.CLASSIFIABLE);
        properties = contentAspect.getProperties(userName, password, siteName, plainDoc);
        values = contentAspect.getValues(properties, "cm:categories");
        Assert.assertTrue(values.isEmpty());
    }
    
    @Test
    public void addAudioAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addAspect(userName, password, siteName, plainDoc, DocumentAspect.AUDIO);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);       
        Assert.assertTrue(properties.toString().contains(DocumentAspect.AUDIO.getProperty()));
    }
    
    @Test
    public void addExifAspect() throws Exception
    {
        String siteName = "siteDocNew" + System.currentTimeMillis();
        String userName = "cmisUser" + System.currentTimeMillis();
        String plainDoc = "plain" +  System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, plainDoc, plainDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        contentAspect.addAspect(userName, password, siteName, plainDoc, DocumentAspect.EXIF);
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, plainDoc);       
        Assert.assertTrue(properties.toString().contains(DocumentAspect.EXIF.getProperty()));
    }
    
    @Test
    public void addTagDocument() throws Exception
    {
        String siteName = "siteTag" + System.currentTimeMillis();
        String userName = "tagUser" + System.currentTimeMillis();
        String tagDoc = "tagDoc" +  System.currentTimeMillis();
        String tag1 = "tag1" + System.currentTimeMillis();
        userService.create(admin, admin, userName, password, password);
        site.create(userName,
                    password,
                    "mydomain",
                    siteName, 
                    "my site description", 
                    Visibility.PUBLIC);
        Document doc1 = content.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, tagDoc, tagDoc);
        Assert.assertFalse(doc1.getId().isEmpty());
        Assert.assertTrue(content.addSingleTag(userName, password, siteName, tagDoc, tag1));
        List<Property<?>> properties = contentAspect.getProperties(userName, password, siteName, tagDoc);       
        Assert.assertTrue(properties.toString().contains(DocumentAspect.TAGGABLE.getProperty()));
        String tagNodeRef = content.getTagNodeRef(userName, password, siteName, tagDoc, tag1);
        List<?> values = contentAspect.getValues(properties, "cm:taggable");
        Assert.assertTrue(values.contains(tagNodeRef));
    }
}
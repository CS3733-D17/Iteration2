package com.slackers.inc.Boundary.BoundaryControllers;



import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by John's New HP on 3/24/2017.
 */
public class CsvWriter implements Closeable,Flushable,AutoCloseable {

    private OutputStream stream;
    private boolean isInitialized;
    private List<String> foundColumns;
    private List<String> foundSubColumns;
    private Set<String> ignored;
    private Charset charset;
    private Class type;
    private String emptyValue;


    /**
     * Constructor for a Csv formatted output writer.
     * The output argument must specify a valid OutputStream {@link OutputStream} that is open.
     * The writer defaults to the ASCII charset for writing.
     * The writer ignores the <code>getClass</code> method by default.
     * The default empty value is the empty string.
     * @param  outputStream  the base stream to output the csv information on
     */
    public CsvWriter(OutputStream outputStream)
    {
        this.stream = outputStream;
        this.isInitialized = false;
        this.foundColumns = new ArrayList<>();
        this.foundSubColumns = new ArrayList<>();
        this.charset = StandardCharsets.US_ASCII;
        this.type = null;
        this.ignored = new HashSet<>();
        this.emptyValue="";
    }

    /**
     * Constructor for a Csv formatted output writer.
     * This constructor creates the output stream internally.
     * The writer defaults to the ASCII charset for writing.
     * The writer ignores the <code>getClass</code> method by default.
     * The default empty value is the empty string.
     * @throws FileNotFoundException if the fileoutputstream cannot be created
     * @param  filename  path to a valid file
     */
    public CsvWriter(String filename) throws FileNotFoundException {
        this.stream = new FileOutputStream(filename);
        this.isInitialized = false;
        this.foundColumns = new ArrayList<>();
        this.foundSubColumns = new ArrayList<>();
        this.charset = StandardCharsets.US_ASCII;
        this.type = null;
        this.ignored = new HashSet<>();
        this.emptyValue="";
    }

    /**
     * Getter for the charset for this CsvWriter
     * @return the charset used by this writer
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Setter for the charset for this CsvWritter
     * @param  charset Charset (@link Charset) used by this writer
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Getter for the empty string value for this CsvWritter
     * @return string used by this writer for a value that is null or not reachable
     */
    public String getEmptyValue() {
        return emptyValue;
    }

    /**
     * Setter for the empty string value for this CsvWritter
     * @param  emptyValue string used by this writer for a value that is null or not reachable
     */
    public void setEmptyValue(String emptyValue) {
        this.emptyValue = emptyValue;
    }

    /**
     * Adder to ignore a specified getter method in the initialized class
     * @param  methodName name of the getter method name to exclude from printing
     */
    public void addIgnoredGetMethod(String methodName)
    {
        this.ignored.add(methodName);
    }
    /**
     * Remover to un-ignore a specified getter method in the initialized class
     * @param  methodName name of the getter method name to include in printing
     */
    public void removeIgnoredGetMethod(String methodName)
    {
        this.ignored.remove(methodName);
    }

    /**
     * Resets the writer to accept another type to allow reuse
     */
    public void reset()
    {
        this.isInitialized = false;
        this.foundColumns.clear();
        this.ignored.clear();
        this.foundSubColumns.clear();
    }

    /**
     * Wrapper for init(Class outputType) that takes an Object rather than a Class.
     * @param  outputObject a object of the type to use for the CSV format template
     */
    public final void init(Object outputObject) {
        this.init(outputObject.getClass());
    }


    /**
     * Initializes the CsvWriter for outputting data in CSV formatting for the class passed in
     * the argument outputType. The argument output type should be the super class for all the objects
     * that will be passed in to print
     * <p>
     * This method examines the public methods of the Class with no parameters.  If a method in the
     * Class starts with "get" followed by an uppercase character that method is assumed to be a
     * getter method and is saved into the template for the columns the CSV writer will produce.
     * The column header names are the names of the getter method minus the leading "get" sorted into
     * alphabetical order.
     * <p>
     * By default the <code>getClass</code> method in the specified Class is ignored.
     * @param  outputType class of the type to use in the CSV format template
     */
    public final void init(Class outputType)
    {
        this.foundColumns.clear();
        this.foundSubColumns.clear();
        this.ignored.clear();
        this.ignored.add("getClass");
        isInitialized = false;
        Method[] methods = outputType.getMethods();
        for (Method m : methods)
        {
            if (m.getParameterCount()==0 && Modifier.isPublic(m.getModifiers()))
            {
                String methodName = m.getName();
                if (methodName.substring(0,3).equals("get") && Character.isUpperCase(methodName.charAt(3)))
                {
                    if (!this.ignored.contains(methodName)&&!this.foundColumns.contains(methodName.substring(3)))
                        foundColumns.add(methodName.substring(3));
                }
            }
        }
        foundColumns.sort(String.CASE_INSENSITIVE_ORDER);
        this.type = outputType;
        this.isInitialized = true;
    }
    /**
     * Initializes the CsvWriter for outputting data in CSV formatting for the class passed in
     * the argument outputType.  This method allows subclasses individual data not in the super class
     * to be printed in addition to the super classes information.
     * <p>
     * This method examines the public methods of the Class with no parameters.  If a method in the
     * Class starts with "get" followed by an uppercase character that method is assumed to be a
     * getter method and is saved into the template for the columns the CSV writer will produce.
     * The column header names are the names of the getter method minus the leading "get" sorted into
     * alphabetical order.
     * <p>
     * By default the <code>getClass</code> method in the specified Class is ignored.
     * @throws IllegalStateException if the writer was not initialized or the argument subType is not
     * a subtype of the original class passed into the init method.
     *
     * @param  subType subclass of the class used in the init method
     */
    public final void initSubtype(Class subType)
    {
        if (!isInitialized)
        {
            throw new IllegalStateException("Must have initialized writer to a target type");
        }
        if (!(this.type.isAssignableFrom(subType)) || this.type.equals(subType)) // invalid object class
        {
            throw new IllegalArgumentException("Object must be subclass of the initialized type");
        }
        Method[] methods = subType.getMethods();
        for (Method m : methods)
        {
            if (m.getParameterCount()==0 && Modifier.isPublic(m.getModifiers()))
            {
                String methodName = m.getName();
                if (methodName.substring(0,3).equals("get") && Character.isUpperCase(methodName.charAt(3)))
                {
                    if (!this.ignored.contains(methodName) && !this.foundColumns.contains(methodName.substring(3))
                            && !this.foundSubColumns.contains(methodName.substring(3))) {
                        foundSubColumns.add(methodName.substring(3));
                    }
                }
            }
        }
        foundSubColumns.sort(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Writes the header line for the Csv output to the output stream.
     *
     * @throws IllegalStateException if the writer was not initialized
     * to a template via the <code>init</code> method of CsvWriter
     */
    public final void writeColumnHeader() throws IOException {
        if (!this.isInitialized)
            throw new IllegalStateException("Must have initialized writer to a target type");

        List<String> cols = new LinkedList<>();
        
        for (String s : this.foundColumns)
        {
            if (this.ignored.contains("get"+s))
                continue;
            cols.add(s);
        }
        for (String s : this.foundSubColumns)
        {
            if (this.ignored.contains("get"+s))
                continue;
            cols.add(s);
        }
        
        String header = "\""+String.join("\",\"", cols)+"\"";

        header = header + "\n";

        this.stream.write(header.getBytes(this.charset));
    }

    /**
     * Writes a single Csv line to the output stream reflecting the values in the argument
     * entry.  All the common values of the super type are printed first and then the values of the
     * subtypes are printed afterwards.
     * <p>
     * Automatically formats output correctly for single vs. null vs. array vs. collection output of each getter.
     * @throws IOException if the underlying stream had an error during writing
     * @throws IllegalStateException if the object passed in is not the type of the template
     * class or a derived subclass
     * @param  entry the object to write to the Csv file
     */
    public void write(Object entry) throws IOException {

        if (!(this.type.isAssignableFrom(entry.getClass()) || this.type.equals(entry.getClass()))) // invalid object class
        {
            throw new IllegalArgumentException("Object must be of the initialized type");
        }

        List<String> values = new LinkedList<>();

        for (String s : this.foundColumns)
        {
            if (this.ignored.contains("get"+s))
                continue;
            try {
                Method method = entry.getClass().getMethod("get" + s, null);
                Object result = method.invoke(entry);
                if (Object[].class.isAssignableFrom(method.getReturnType()))
                {
                    values.add(Arrays.toString((Object[])result));
                }
                else if (Collection.class.isAssignableFrom(method.getReturnType()))
                {
                    values.add(Arrays.toString((Object[])result));
                }
                else if (result == null)
                {
                    values.add(this.emptyValue);
                }
                else {
                    values.add(result.toString());
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Object must be of the initialized type");
            }
        }

        for (String s : this.foundSubColumns)
        {
            if (this.ignored.contains("get"+s))
                continue;
            try {
                Method method = entry.getClass().getMethod("get" + s, null);
                Object result = method.invoke(entry);
                if (Object[].class.isAssignableFrom(method.getReturnType()))
                {
                    values.add(Arrays.toString((Object[])result));
                }
                else if (Collection.class.isAssignableFrom(method.getReturnType()))
                {
                    values.add(Arrays.toString((Object[])result));
                }
                else if (result == null)
                {
                    values.add(this.emptyValue);
                }
                else {
                    values.add(result.toString());
                }
            } catch (Exception e) {
                values.add(this.emptyValue);
            }
        }
        String line = "\""+String.join("\",\"", values) + "\""+"\n";
        this.stream.write(line.getBytes(this.charset));
    }

    /**
     * Writes a multiple Csv line to the output stream reflecting the values in the argument
     * entries.  Essentially a wrapper for <code>write(Object entry)</code> that allows multiple
     * to be written at once.
     * <p>
     * Automatically formats output correctly for single vs. array vs. collection output of each getter.
     * @throws IOException if the underlying stream had an error during writing
     * @throws IllegalStateException if any object passed in is not the type of the template
     * class or a derived subclass
     * @param  entries the object collection to write to the Csv file
     */
    public <T extends Object> void write(Collection<T> entries) throws IOException {
        if (!isInitialized)
        {
            if (!entries.isEmpty())
            {
                this.init(entries.toArray()[0]);
                this.writeColumnHeader();
            }
        }
        for (T entry : entries)
        {
            this.write(entry);
        }
    }

    /**
     * Closes this stream and the underlying stream
     * @throws IOException if the underlying stream had an error during closing
     */
    @Override
    public void close() throws IOException {
        if (this.stream!=null)
            this.stream.close();
    }

    /**
     * Flushes this stream and the underlying stream
     * @throws IOException if the underlying stream had an error during flushing
     */
    @Override
    public void flush() throws IOException {
        if (this.stream!=null)
            this.stream.flush();
    }
}
/*
CsvWriter out = new CsvWriter(##OUTSTREAM);
        
out.init(com.slackers.inc.database.entities.Label.class);
out.initSubtype(com.slackers.inc.database.entities.BeerLabel.class);
out.initSubtype(com.slackers.inc.database.entities.WineLabel.class);
out.initSubtype(com.slackers.inc.database.entities.DistilledLabel.class);

out.addIgnoredGetMethod("getEntityNameTypePairs");
out.addIgnoredGetMethod("getEntityValues");
out.addIgnoredGetMethod("getUpdatableEntityValues");
out.addIgnoredGetMethod("getApproval");

out.writeColumnHeader();
out.write(##LIST);
out.flush();
*/

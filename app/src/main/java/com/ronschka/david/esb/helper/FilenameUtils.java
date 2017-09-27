package com.ronschka.david.esb.helper;

public final class FilenameUtils {
    /**
     * The extension separator character.
     *
     * @since Commons IO 1.4
     */
    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * Gets the extension of a filename.
     * <p/>
     * This method returns the textual part of the filename after the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt      --> "txt"
     * a/b/c.jpg    --> "jpg"
     * a/b.txt/c    --> ""
     * a/b/c        --> ""
     * </pre>
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the filename is {@code null}.
     */
    public static String getExtension(final String filename) {
        if (filename == null)
            return null;
        final int index = indexOfExtension(filename);
        if (index == -1)
            return "";
        else
            return filename.substring(index + 1);
    }


    /**
     * Returns the index of the last directory separator character.
     * <p/>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    private static int indexOfLastSeparator(final String filename) {
        if (filename == null)
            return -1;
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p/>
     * This method also checks that there is no directory separator after the last dot.
     * To do this it uses {@link #indexOfLastSeparator(String)} which will
     * handle a file in either Unix or Windows format.
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    private static int indexOfExtension(final String filename) {
        if (filename == null)
            return -1;
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }
    //-----------------------------------------------------------------------

    /**
     * Removes the extension from a filename.
     * <p/>
     * This method returns the textual part of the filename before the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt    --> foo
     * a\b\c.jpg  --> a\b\c
     * a\b\c      --> a\b\c
     * a.b\c      --> a.b\c
     * </pre>
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to query, null returns null
     * @return the filename minus the extension
     */
    public static String removeExtension(final String filename) {
        if (filename == null)
            return null;
        final int index = indexOfExtension(filename);
        if (index == -1)
            return filename;
        else
            return filename.substring(0, index);
    }
}

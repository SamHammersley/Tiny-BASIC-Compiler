package uk.ac.tees.syntax.compiler.x86_64;

import java.util.*;

import static uk.ac.tees.syntax.compiler.x86_64.X86_64CompilerConstants.INDENTATION;

/**
 * Represents a data section of an x86-64 Netwide Assembler program.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class X86_64DataSection {

    /**
     * The {@link uk.ac.tees.syntax.compiler.x86_64.X86_64CompilerConstants.DataSectionType} of this section.
     */
    private final X86_64CompilerConstants.DataSectionType type;

    /**
     * Data values mapped corresponding {@link DataEntry}s.
     */
    private final Map<String, DataEntry> data = new HashMap<>();

    X86_64DataSection(X86_64CompilerConstants.DataSectionType type) {
        this.type = type;
    }

    /**
     * Associates the given label with value (and it's pseudo-instruction).
     *
     * @param label the label associated with the value.
     * @param pseudoInstruction the pseudo-instruction for the value.
     * @param value the value associated with the label.
     */
    void addPredefinedEntry(String label, String pseudoInstruction, String value) {
        data.put(value, new DataEntry(value, label, pseudoInstruction));
    }

    /**
     * Associates the given value, and pseudo-instruction, with a generated label.
     *
     * @param value the value of the entry.
     * @param pseudoInstruction the pseudo-instruction for the value.
     */
    void addEntry(String value, String pseudoInstruction) {
        String label = type.getLabel().substring(1) + data.size();

        data.putIfAbsent(value, new DataEntry(value, label, pseudoInstruction));
    }

    /**
     * Gets a label for the given value.
     *
     * @param value the value to get a label for.
     * @return the label associated with the given value.
     */
    String getLabel(String value) {
        return data.get(value).label;
    }

    @Override
    public String toString() {
        addPredefinedEntry("new_line_char", "db", "0x0A");

        StringBuilder readOnlyBuilder = new StringBuilder("section .rodata\n");

        for (DataEntry entry : data.values()) {
            readOnlyBuilder.append(INDENTATION).append(entry).append('\n');
        }

        return readOnlyBuilder.append('\n').toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof X86_64DataSection)) {
            return false;
        }

        return ((X86_64DataSection) object).type.equals(type);
    }

    /**
     * Represents an entry in a data section.
     *
     * Each entry has a value, label and pseudo-instruction.
     *
     * Pseudo-instructions for data entries are listed in the NASM manual, some are listed below:
     * <l>
     *     <li>db - define bytes</li>
     *     <li>dw - define word (16-bits)</li>
     *     <li>dd - define double (32-bits)</li>
     *     <li>dq - define quad (64-bits)</li>
     *     <li>equ - define symbol with constant value</li>
     * </l>
     *
     * @see <a href="https://www.nasm.us/doc/nasmdoc3.html#section-3.2">NASM Manual</a>
     */
    private final class DataEntry {

        private final String value;

        private final String label;

        private final String pseudoInstruction;

        private DataEntry(String value, String label, String pseudoInstruction) {
            this.value = value;
            this.label = label;
            this.pseudoInstruction = pseudoInstruction;
        }

        @Override
        public String toString() {
            return label + ": " + pseudoInstruction + " " + value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, label, pseudoInstruction);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof DataEntry)) {
                return false;
            }

            DataEntry other = (DataEntry) object;

            return other.value.equals(value)
                    && other.label.equals(label)
                    && other.pseudoInstruction.equals(pseudoInstruction);
        }

    }

}
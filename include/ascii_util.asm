section .text

decimal_to_ascii:
    pop r8
    pop rax
    mov r9, 10
    mov r10, 0
add_ascii_offset_loop:
    xor rdx, rdx
    idiv r9
    add rdx, 48
    or r10, rdx
    shl r10, 8
    cmp rax, 0
    jne add_ascii_offset_loop
    push r10
    push r8
    ret
ascii_to_decimal:
    pop r8
    pop r9
    mov r10, 0
sub_ascii_offset_loop:
    mov rax, r9
    and rax, 0xFF
    cmp rax, 48
    jl skip_digit
    sub rax, 48
    and rax, 0xF
    or r10, rax
    shl r10, 4
    skip_digit:
    shr r9, 8
    cmp r9, 0
    jne sub_ascii_offset_loop
    shr r10, 4
    push r10
    push r8
    ret

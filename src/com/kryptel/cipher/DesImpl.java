/*******************************************************************************

  Product:       Kryptel/Java
  File:          DesImpl.java
  Description:   https://www.kryptel.com/articles/developers/java/cipher.php

  Copyright (c) 2017 Inv Softworks LLC,    http://www.kryptel.com

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*******************************************************************************/


package com.kryptel.cipher;


import static com.kryptel.bslx.Conversions.FromBytes;
import static com.kryptel.bslx.Conversions.ToBytes;


abstract class DesImpl extends BlockCipherBase {
	DesImpl(long capabilities) {
		super(capabilities);
	}


  //
  // Implementation
  //

	static final int DES_BLOCK_SIZE = 8;
	static final int DES_ROUNDS = 16;


  private static int[][] DesTable = {
    { 0x08000820, 0x00000800, 0x00020000, 0x08020820, 0x08000000, 0x08000820, 0x00000020, 0x08000000,
      0x00020020, 0x08020000, 0x08020820, 0x00020800, 0x08020800, 0x00020820, 0x00000800, 0x00000020,
      0x08020000, 0x08000020, 0x08000800, 0x00000820, 0x00020800, 0x00020020, 0x08020020, 0x08020800,
      0x00000820, 0x00000000, 0x00000000, 0x08020020, 0x08000020, 0x08000800, 0x00020820, 0x00020000,
      0x00020820, 0x00020000, 0x08020800, 0x00000800, 0x00000020, 0x08020020, 0x00000800, 0x00020820,
      0x08000800, 0x00000020, 0x08000020, 0x08020000, 0x08020020, 0x08000000, 0x00020000, 0x08000820,
      0x00000000, 0x08020820, 0x00020020, 0x08000020, 0x08020000, 0x08000800, 0x08000820, 0x00000000,
      0x08020820, 0x00020800, 0x00020800, 0x00000820, 0x00000820, 0x00020020, 0x08000000, 0x08020800 },

    { 0x00100000, 0x02100001, 0x02000401, 0x00000000, 0x00000400, 0x02000401, 0x00100401, 0x02100400,
      0x02100401, 0x00100000, 0x00000000, 0x02000001, 0x00000001, 0x02000000, 0x02100001, 0x00000401,
      0x02000400, 0x00100401, 0x00100001, 0x02000400, 0x02000001, 0x02100000, 0x02100400, 0x00100001,
      0x02100000, 0x00000400, 0x00000401, 0x02100401, 0x00100400, 0x00000001, 0x02000000, 0x00100400,
      0x02000000, 0x00100400, 0x00100000, 0x02000401, 0x02000401, 0x02100001, 0x02100001, 0x00000001,
      0x00100001, 0x02000000, 0x02000400, 0x00100000, 0x02100400, 0x00000401, 0x00100401, 0x02100400,
      0x00000401, 0x02000001, 0x02100401, 0x02100000, 0x00100400, 0x00000000, 0x00000001, 0x02100401,
      0x00000000, 0x00100401, 0x02100000, 0x00000400, 0x02000001, 0x02000400, 0x00000400, 0x00100001 },

    { 0x10000008, 0x10200000, 0x00002000, 0x10202008, 0x10200000, 0x00000008, 0x10202008, 0x00200000,
      0x10002000, 0x00202008, 0x00200000, 0x10000008, 0x00200008, 0x10002000, 0x10000000, 0x00002008,
      0x00000000, 0x00200008, 0x10002008, 0x00002000, 0x00202000, 0x10002008, 0x00000008, 0x10200008,
      0x10200008, 0x00000000, 0x00202008, 0x10202000, 0x00002008, 0x00202000, 0x10202000, 0x10000000,
      0x10002000, 0x00000008, 0x10200008, 0x00202000, 0x10202008, 0x00200000, 0x00002008, 0x10000008,
      0x00200000, 0x10002000, 0x10000000, 0x00002008, 0x10000008, 0x10202008, 0x00202000, 0x10200000,
      0x00202008, 0x10202000, 0x00000000, 0x10200008, 0x00000008, 0x00002000, 0x10200000, 0x00202008,
      0x00002000, 0x00200008, 0x10002008, 0x00000000, 0x10202000, 0x10000000, 0x00200008, 0x10002008 },

    { 0x00000080, 0x01040080, 0x01040000, 0x21000080, 0x00040000, 0x00000080, 0x20000000, 0x01040000,
      0x20040080, 0x00040000, 0x01000080, 0x20040080, 0x21000080, 0x21040000, 0x00040080, 0x20000000,
      0x01000000, 0x20040000, 0x20040000, 0x00000000, 0x20000080, 0x21040080, 0x21040080, 0x01000080,
      0x21040000, 0x20000080, 0x00000000, 0x21000000, 0x01040080, 0x01000000, 0x21000000, 0x00040080,
      0x00040000, 0x21000080, 0x00000080, 0x01000000, 0x20000000, 0x01040000, 0x21000080, 0x20040080,
      0x01000080, 0x20000000, 0x21040000, 0x01040080, 0x20040080, 0x00000080, 0x01000000, 0x21040000,
      0x21040080, 0x00040080, 0x21000000, 0x21040080, 0x01040000, 0x00000000, 0x20040000, 0x21000000,
      0x00040080, 0x01000080, 0x20000080, 0x00040000, 0x00000000, 0x20040000, 0x01040080, 0x20000080 },

    { 0x80401000, 0x80001040, 0x80001040, 0x00000040, 0x00401040, 0x80400040, 0x80400000, 0x80001000,
      0x00000000, 0x00401000, 0x00401000, 0x80401040, 0x80000040, 0x00000000, 0x00400040, 0x80400000,
      0x80000000, 0x00001000, 0x00400000, 0x80401000, 0x00000040, 0x00400000, 0x80001000, 0x00001040,
      0x80400040, 0x80000000, 0x00001040, 0x00400040, 0x00001000, 0x00401040, 0x80401040, 0x80000040,
      0x00400040, 0x80400000, 0x00401000, 0x80401040, 0x80000040, 0x00000000, 0x00000000, 0x00401000,
      0x00001040, 0x00400040, 0x80400040, 0x80000000, 0x80401000, 0x80001040, 0x80001040, 0x00000040,
      0x80401040, 0x80000040, 0x80000000, 0x00001000, 0x80400000, 0x80001000, 0x00401040, 0x80400040,
      0x80001000, 0x00001040, 0x00400000, 0x80401000, 0x00000040, 0x00400000, 0x00001000, 0x00401040 },

    { 0x00000104, 0x04010100, 0x00000000, 0x04010004, 0x04000100, 0x00000000, 0x00010104, 0x04000100,
      0x00010004, 0x04000004, 0x04000004, 0x00010000, 0x04010104, 0x00010004, 0x04010000, 0x00000104,
      0x04000000, 0x00000004, 0x04010100, 0x00000100, 0x00010100, 0x04010000, 0x04010004, 0x00010104,
      0x04000104, 0x00010100, 0x00010000, 0x04000104, 0x00000004, 0x04010104, 0x00000100, 0x04000000,
      0x04010100, 0x04000000, 0x00010004, 0x00000104, 0x00010000, 0x04010100, 0x04000100, 0x00000000,
      0x00000100, 0x00010004, 0x04010104, 0x04000100, 0x04000004, 0x00000100, 0x00000000, 0x04010004,
      0x04000104, 0x00010000, 0x04000000, 0x04010104, 0x00000004, 0x00010104, 0x00010100, 0x04000004,
      0x04010000, 0x04000104, 0x00000104, 0x04010000, 0x00010104, 0x00000004, 0x04010004, 0x00010100 },

    { 0x40084010, 0x40004000, 0x00004000, 0x00084010, 0x00080000, 0x00000010, 0x40080010, 0x40004010,
      0x40000010, 0x40084010, 0x40084000, 0x40000000, 0x40004000, 0x00080000, 0x00000010, 0x40080010,
      0x00084000, 0x00080010, 0x40004010, 0x00000000, 0x40000000, 0x00004000, 0x00084010, 0x40080000,
      0x00080010, 0x40000010, 0x00000000, 0x00084000, 0x00004010, 0x40084000, 0x40080000, 0x00004010,
      0x00000000, 0x00084010, 0x40080010, 0x00080000, 0x40004010, 0x40080000, 0x40084000, 0x00004000,
      0x40080000, 0x40004000, 0x00000010, 0x40084010, 0x00084010, 0x00000010, 0x00004000, 0x40000000,
      0x00004010, 0x40084000, 0x00080000, 0x40000010, 0x00080010, 0x40004010, 0x40000010, 0x00080010,
      0x00084000, 0x00000000, 0x40004000, 0x00004010, 0x40000000, 0x40080010, 0x40084010, 0x00084000 },

    { 0x00808200, 0x00000000, 0x00008000, 0x00808202, 0x00808002, 0x00008202, 0x00000002, 0x00008000,
      0x00000200, 0x00808200, 0x00808202, 0x00000200, 0x00800202, 0x00808002, 0x00800000, 0x00000002,
      0x00000202, 0x00800200, 0x00800200, 0x00008200, 0x00008200, 0x00808000, 0x00808000, 0x00800202,
      0x00008002, 0x00800002, 0x00800002, 0x00008002, 0x00000000, 0x00000202, 0x00008202, 0x00800000,
      0x00008000, 0x00808202, 0x00000002, 0x00808000, 0x00808200, 0x00800000, 0x00800000, 0x00000200,
      0x00808002, 0x00008000, 0x00008200, 0x00800002, 0x00000200, 0x00000002, 0x00800202, 0x00008202,
      0x00808202, 0x00008002, 0x00808000, 0x00800202, 0x00800002, 0x00000202, 0x00008202, 0x00808200,
      0x00000202, 0x00800200, 0x00800200, 0x00000000, 0x00008002, 0x00008200, 0x00000000, 0x00808002 } };

  private static int[][] cTable = {
    { 0x00000000, 0x00010000, 0x00000008, 0x00010008, 0x00000080, 0x00010080, 0x00000088, 0x00010088,
      0x00000000, 0x00010000, 0x00000008, 0x00010008, 0x00000080, 0x00010080, 0x00000088, 0x00010088,
      0x00100000, 0x00110000, 0x00100008, 0x00110008, 0x00100080, 0x00110080, 0x00100088, 0x00110088,
      0x00100000, 0x00110000, 0x00100008, 0x00110008, 0x00100080, 0x00110080, 0x00100088, 0x00110088,
      0x00000800, 0x00010800, 0x00000808, 0x00010808, 0x00000880, 0x00010880, 0x00000888, 0x00010888,
      0x00000800, 0x00010800, 0x00000808, 0x00010808, 0x00000880, 0x00010880, 0x00000888, 0x00010888,
      0x00100800, 0x00110800, 0x00100808, 0x00110808, 0x00100880, 0x00110880, 0x00100888, 0x00110888,
      0x00100800, 0x00110800, 0x00100808, 0x00110808, 0x00100880, 0x00110880, 0x00100888, 0x00110888,
      0x00000000, 0x00010000, 0x00000008, 0x00010008, 0x00000080, 0x00010080, 0x00000088, 0x00010088,
      0x00000000, 0x00010000, 0x00000008, 0x00010008, 0x00000080, 0x00010080, 0x00000088, 0x00010088,
      0x00100000, 0x00110000, 0x00100008, 0x00110008, 0x00100080, 0x00110080, 0x00100088, 0x00110088,
      0x00100000, 0x00110000, 0x00100008, 0x00110008, 0x00100080, 0x00110080, 0x00100088, 0x00110088,
      0x00000800, 0x00010800, 0x00000808, 0x00010808, 0x00000880, 0x00010880, 0x00000888, 0x00010888,
      0x00000800, 0x00010800, 0x00000808, 0x00010808, 0x00000880, 0x00010880, 0x00000888, 0x00010888,
      0x00100800, 0x00110800, 0x00100808, 0x00110808, 0x00100880, 0x00110880, 0x00100888, 0x00110888,
      0x00100800, 0x00110800, 0x00100808, 0x00110808, 0x00100880, 0x00110880, 0x00100888, 0x00110888 },

    { 0x00000000, 0x00002000, 0x00000004, 0x00002004, 0x00000400, 0x00002400, 0x00000404, 0x00002404,
      0x00000000, 0x00002000, 0x00000004, 0x00002004, 0x00000400, 0x00002400, 0x00000404, 0x00002404,
      0x00400000, 0x00402000, 0x00400004, 0x00402004, 0x00400400, 0x00402400, 0x00400404, 0x00402404,
      0x00400000, 0x00402000, 0x00400004, 0x00402004, 0x00400400, 0x00402400, 0x00400404, 0x00402404,
      0x00000020, 0x00002020, 0x00000024, 0x00002024, 0x00000420, 0x00002420, 0x00000424, 0x00002424,
      0x00000020, 0x00002020, 0x00000024, 0x00002024, 0x00000420, 0x00002420, 0x00000424, 0x00002424,
      0x00400020, 0x00402020, 0x00400024, 0x00402024, 0x00400420, 0x00402420, 0x00400424, 0x00402424,
      0x00400020, 0x00402020, 0x00400024, 0x00402024, 0x00400420, 0x00402420, 0x00400424, 0x00402424,
      0x00008000, 0x0000A000, 0x00008004, 0x0000A004, 0x00008400, 0x0000A400, 0x00008404, 0x0000A404,
      0x00008000, 0x0000A000, 0x00008004, 0x0000A004, 0x00008400, 0x0000A400, 0x00008404, 0x0000A404,
      0x00408000, 0x0040A000, 0x00408004, 0x0040A004, 0x00408400, 0x0040A400, 0x00408404, 0x0040A404,
      0x00408000, 0x0040A000, 0x00408004, 0x0040A004, 0x00408400, 0x0040A400, 0x00408404, 0x0040A404,
      0x00008020, 0x0000A020, 0x00008024, 0x0000A024, 0x00008420, 0x0000A420, 0x00008424, 0x0000A424,
      0x00008020, 0x0000A020, 0x00008024, 0x0000A024, 0x00008420, 0x0000A420, 0x00008424, 0x0000A424,
      0x00408020, 0x0040A020, 0x00408024, 0x0040A024, 0x00408420, 0x0040A420, 0x00408424, 0x0040A424,
      0x00408020, 0x0040A020, 0x00408024, 0x0040A024, 0x00408420, 0x0040A420, 0x00408424, 0x0040A424 },

    { 0x00000000, 0x00800000, 0x00000002, 0x00800002, 0x00000200, 0x00800200, 0x00000202, 0x00800202,
      0x00200000, 0x00A00000, 0x00200002, 0x00A00002, 0x00200200, 0x00A00200, 0x00200202, 0x00A00202,
      0x00001000, 0x00801000, 0x00001002, 0x00801002, 0x00001200, 0x00801200, 0x00001202, 0x00801202,
      0x00201000, 0x00A01000, 0x00201002, 0x00A01002, 0x00201200, 0x00A01200, 0x00201202, 0x00A01202,
      0x00000000, 0x00800000, 0x00000002, 0x00800002, 0x00000200, 0x00800200, 0x00000202, 0x00800202,
      0x00200000, 0x00A00000, 0x00200002, 0x00A00002, 0x00200200, 0x00A00200, 0x00200202, 0x00A00202,
      0x00001000, 0x00801000, 0x00001002, 0x00801002, 0x00001200, 0x00801200, 0x00001202, 0x00801202,
      0x00201000, 0x00A01000, 0x00201002, 0x00A01002, 0x00201200, 0x00A01200, 0x00201202, 0x00A01202,
      0x00000040, 0x00800040, 0x00000042, 0x00800042, 0x00000240, 0x00800240, 0x00000242, 0x00800242,
      0x00200040, 0x00A00040, 0x00200042, 0x00A00042, 0x00200240, 0x00A00240, 0x00200242, 0x00A00242,
      0x00001040, 0x00801040, 0x00001042, 0x00801042, 0x00001240, 0x00801240, 0x00001242, 0x00801242,
      0x00201040, 0x00A01040, 0x00201042, 0x00A01042, 0x00201240, 0x00A01240, 0x00201242, 0x00A01242,
      0x00000040, 0x00800040, 0x00000042, 0x00800042, 0x00000240, 0x00800240, 0x00000242, 0x00800242,
      0x00200040, 0x00A00040, 0x00200042, 0x00A00042, 0x00200240, 0x00A00240, 0x00200242, 0x00A00242,
      0x00001040, 0x00801040, 0x00001042, 0x00801042, 0x00001240, 0x00801240, 0x00001242, 0x00801242,
      0x00201040, 0x00A01040, 0x00201042, 0x00A01042, 0x00201240, 0x00A01240, 0x00201242, 0x00A01242 },

    { 0x00000000, 0x00000010, 0x00004000, 0x00004010, 0x00040000, 0x00040010, 0x00044000, 0x00044010,
      0x00000100, 0x00000110, 0x00004100, 0x00004110, 0x00040100, 0x00040110, 0x00044100, 0x00044110,
      0x00020000, 0x00020010, 0x00024000, 0x00024010, 0x00060000, 0x00060010, 0x00064000, 0x00064010,
      0x00020100, 0x00020110, 0x00024100, 0x00024110, 0x00060100, 0x00060110, 0x00064100, 0x00064110,
      0x00000001, 0x00000011, 0x00004001, 0x00004011, 0x00040001, 0x00040011, 0x00044001, 0x00044011,
      0x00000101, 0x00000111, 0x00004101, 0x00004111, 0x00040101, 0x00040111, 0x00044101, 0x00044111,
      0x00020001, 0x00020011, 0x00024001, 0x00024011, 0x00060001, 0x00060011, 0x00064001, 0x00064011,
      0x00020101, 0x00020111, 0x00024101, 0x00024111, 0x00060101, 0x00060111, 0x00064101, 0x00064111,
      0x00080000, 0x00080010, 0x00084000, 0x00084010, 0x000C0000, 0x000C0010, 0x000C4000, 0x000C4010,
      0x00080100, 0x00080110, 0x00084100, 0x00084110, 0x000C0100, 0x000C0110, 0x000C4100, 0x000C4110,
      0x000A0000, 0x000A0010, 0x000A4000, 0x000A4010, 0x000E0000, 0x000E0010, 0x000E4000, 0x000E4010,
      0x000A0100, 0x000A0110, 0x000A4100, 0x000A4110, 0x000E0100, 0x000E0110, 0x000E4100, 0x000E4110,
      0x00080001, 0x00080011, 0x00084001, 0x00084011, 0x000C0001, 0x000C0011, 0x000C4001, 0x000C4011,
      0x00080101, 0x00080111, 0x00084101, 0x00084111, 0x000C0101, 0x000C0111, 0x000C4101, 0x000C4111,
      0x000A0001, 0x000A0011, 0x000A4001, 0x000A4011, 0x000E0001, 0x000E0011, 0x000E4001, 0x000E4011,
      0x000A0101, 0x000A0111, 0x000A4101, 0x000A4111, 0x000E0101, 0x000E0111, 0x000E4101, 0x000E4111 } };


  private static int[][] dTable = {
    { 0x00000000, 0x00000100, 0x00040000, 0x00040100, 0x00000000, 0x00000100, 0x00040000, 0x00040100,
      0x00000040, 0x00000140, 0x00040040, 0x00040140, 0x00000040, 0x00000140, 0x00040040, 0x00040140,
      0x00400000, 0x00400100, 0x00440000, 0x00440100, 0x00400000, 0x00400100, 0x00440000, 0x00440100,
      0x00400040, 0x00400140, 0x00440040, 0x00440140, 0x00400040, 0x00400140, 0x00440040, 0x00440140,
      0x00008000, 0x00008100, 0x00048000, 0x00048100, 0x00008000, 0x00008100, 0x00048000, 0x00048100,
      0x00008040, 0x00008140, 0x00048040, 0x00048140, 0x00008040, 0x00008140, 0x00048040, 0x00048140,
      0x00408000, 0x00408100, 0x00448000, 0x00448100, 0x00408000, 0x00408100, 0x00448000, 0x00448100,
      0x00408040, 0x00408140, 0x00448040, 0x00448140, 0x00408040, 0x00408140, 0x00448040, 0x00448140,
      0x00000008, 0x00000108, 0x00040008, 0x00040108, 0x00000008, 0x00000108, 0x00040008, 0x00040108,
      0x00000048, 0x00000148, 0x00040048, 0x00040148, 0x00000048, 0x00000148, 0x00040048, 0x00040148,
      0x00400008, 0x00400108, 0x00440008, 0x00440108, 0x00400008, 0x00400108, 0x00440008, 0x00440108,
      0x00400048, 0x00400148, 0x00440048, 0x00440148, 0x00400048, 0x00400148, 0x00440048, 0x00440148,
      0x00008008, 0x00008108, 0x00048008, 0x00048108, 0x00008008, 0x00008108, 0x00048008, 0x00048108,
      0x00008048, 0x00008148, 0x00048048, 0x00048148, 0x00008048, 0x00008148, 0x00048048, 0x00048148,
      0x00408008, 0x00408108, 0x00448008, 0x00448108, 0x00408008, 0x00408108, 0x00448008, 0x00448108,
      0x00408048, 0x00408148, 0x00448048, 0x00448148, 0x00408048, 0x00408148, 0x00448048, 0x00448148 },

    { 0x00000000, 0x00000400, 0x00001000, 0x00001400, 0x00080000, 0x00080400, 0x00081000, 0x00081400,
      0x00000020, 0x00000420, 0x00001020, 0x00001420, 0x00080020, 0x00080420, 0x00081020, 0x00081420,
      0x00004000, 0x00004400, 0x00005000, 0x00005400, 0x00084000, 0x00084400, 0x00085000, 0x00085400,
      0x00004020, 0x00004420, 0x00005020, 0x00005420, 0x00084020, 0x00084420, 0x00085020, 0x00085420,
      0x00000800, 0x00000C00, 0x00001800, 0x00001C00, 0x00080800, 0x00080C00, 0x00081800, 0x00081C00,
      0x00000820, 0x00000C20, 0x00001820, 0x00001C20, 0x00080820, 0x00080C20, 0x00081820, 0x00081C20,
      0x00004800, 0x00004C00, 0x00005800, 0x00005C00, 0x00084800, 0x00084C00, 0x00085800, 0x00085C00,
      0x00004820, 0x00004C20, 0x00005820, 0x00005C20, 0x00084820, 0x00084C20, 0x00085820, 0x00085C20,
      0x00000000, 0x00000400, 0x00001000, 0x00001400, 0x00080000, 0x00080400, 0x00081000, 0x00081400,
      0x00000020, 0x00000420, 0x00001020, 0x00001420, 0x00080020, 0x00080420, 0x00081020, 0x00081420,
      0x00004000, 0x00004400, 0x00005000, 0x00005400, 0x00084000, 0x00084400, 0x00085000, 0x00085400,
      0x00004020, 0x00004420, 0x00005020, 0x00005420, 0x00084020, 0x00084420, 0x00085020, 0x00085420,
      0x00000800, 0x00000C00, 0x00001800, 0x00001C00, 0x00080800, 0x00080C00, 0x00081800, 0x00081C00,
      0x00000820, 0x00000C20, 0x00001820, 0x00001C20, 0x00080820, 0x00080C20, 0x00081820, 0x00081C20,
      0x00004800, 0x00004C00, 0x00005800, 0x00005C00, 0x00084800, 0x00084C00, 0x00085800, 0x00085C00,
      0x00004820, 0x00004C20, 0x00005820, 0x00005C20, 0x00084820, 0x00084C20, 0x00085820, 0x00085C20 },

    { 0x00000000, 0x00000010, 0x00800000, 0x00800010, 0x00010000, 0x00010010, 0x00810000, 0x00810010,
      0x00000200, 0x00000210, 0x00800200, 0x00800210, 0x00010200, 0x00010210, 0x00810200, 0x00810210,
      0x00000000, 0x00000010, 0x00800000, 0x00800010, 0x00010000, 0x00010010, 0x00810000, 0x00810010,
      0x00000200, 0x00000210, 0x00800200, 0x00800210, 0x00010200, 0x00010210, 0x00810200, 0x00810210,
      0x00100000, 0x00100010, 0x00900000, 0x00900010, 0x00110000, 0x00110010, 0x00910000, 0x00910010,
      0x00100200, 0x00100210, 0x00900200, 0x00900210, 0x00110200, 0x00110210, 0x00910200, 0x00910210,
      0x00100000, 0x00100010, 0x00900000, 0x00900010, 0x00110000, 0x00110010, 0x00910000, 0x00910010,
      0x00100200, 0x00100210, 0x00900200, 0x00900210, 0x00110200, 0x00110210, 0x00910200, 0x00910210,
      0x00000004, 0x00000014, 0x00800004, 0x00800014, 0x00010004, 0x00010014, 0x00810004, 0x00810014,
      0x00000204, 0x00000214, 0x00800204, 0x00800214, 0x00010204, 0x00010214, 0x00810204, 0x00810214,
      0x00000004, 0x00000014, 0x00800004, 0x00800014, 0x00010004, 0x00010014, 0x00810004, 0x00810014,
      0x00000204, 0x00000214, 0x00800204, 0x00800214, 0x00010204, 0x00010214, 0x00810204, 0x00810214,
      0x00100004, 0x00100014, 0x00900004, 0x00900014, 0x00110004, 0x00110014, 0x00910004, 0x00910014,
      0x00100204, 0x00100214, 0x00900204, 0x00900214, 0x00110204, 0x00110214, 0x00910204, 0x00910214,
      0x00100004, 0x00100014, 0x00900004, 0x00900014, 0x00110004, 0x00110014, 0x00910004, 0x00910014,
      0x00100204, 0x00100214, 0x00900204, 0x00900214, 0x00110204, 0x00110214, 0x00910204, 0x00910214 },

    { 0x00000000, 0x00000000, 0x00000080, 0x00000080, 0x00002000, 0x00002000, 0x00002080, 0x00002080,
      0x00000001, 0x00000001, 0x00000081, 0x00000081, 0x00002001, 0x00002001, 0x00002081, 0x00002081,
      0x00200000, 0x00200000, 0x00200080, 0x00200080, 0x00202000, 0x00202000, 0x00202080, 0x00202080,
      0x00200001, 0x00200001, 0x00200081, 0x00200081, 0x00202001, 0x00202001, 0x00202081, 0x00202081,
      0x00020000, 0x00020000, 0x00020080, 0x00020080, 0x00022000, 0x00022000, 0x00022080, 0x00022080,
      0x00020001, 0x00020001, 0x00020081, 0x00020081, 0x00022001, 0x00022001, 0x00022081, 0x00022081,
      0x00220000, 0x00220000, 0x00220080, 0x00220080, 0x00222000, 0x00222000, 0x00222080, 0x00222080,
      0x00220001, 0x00220001, 0x00220081, 0x00220081, 0x00222001, 0x00222001, 0x00222081, 0x00222081,
      0x00000002, 0x00000002, 0x00000082, 0x00000082, 0x00002002, 0x00002002, 0x00002082, 0x00002082,
      0x00000003, 0x00000003, 0x00000083, 0x00000083, 0x00002003, 0x00002003, 0x00002083, 0x00002083,
      0x00200002, 0x00200002, 0x00200082, 0x00200082, 0x00202002, 0x00202002, 0x00202082, 0x00202082,
      0x00200003, 0x00200003, 0x00200083, 0x00200083, 0x00202003, 0x00202003, 0x00202083, 0x00202083,
      0x00020002, 0x00020002, 0x00020082, 0x00020082, 0x00022002, 0x00022002, 0x00022082, 0x00022082,
      0x00020003, 0x00020003, 0x00020083, 0x00020083, 0x00022003, 0x00022003, 0x00022083, 0x00022083,
      0x00220002, 0x00220002, 0x00220082, 0x00220082, 0x00222002, 0x00222002, 0x00222082, 0x00222082,
      0x00220003, 0x00220003, 0x00220083, 0x00220083, 0x00222003, 0x00222003, 0x00222083, 0x00222083 } };

  private static int[] NShifts = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
  
  
  private static int SHL(int x, int n) {
    return (n > 0) ? (x << n) : (x >>> (-n));
  }
  private static int SHR(int x, int n) {
    return (n > 0) ? (x >>> n) : (x << (-n));
  }
  private static int ROL(int x, int n) {
    return (x << n) | (x >>> (32 - n));
  }
  private static int ROR(int x, int n) {
    return (x >>> n) | (x << (32 - n));
  }

  
  private static class RefInt { public int v; }

  private static void PERMUTE(RefInt x, RefInt y, int sh, int mk) {
    int t = (SHR(y.v, sh) ^ x.v) & mk;
    x.v ^= t;
    y.v ^= SHL(t, sh);
  }

  private static void ROUND(RefInt L, int r, int[][] k, int i) {
    int u = r ^ k[0][i];
    int v = r ^ k[1][i];
    L.v ^= ( DesTable[0][ROL(u, 1)  & 0x3F] |
		         DesTable[1][v >>> 3     & 0x3F] |
		         DesTable[2][u >>> 7     & 0x3F] |
		         DesTable[3][v >>> 11    & 0x3F] |
		         DesTable[4][u >>> 15    & 0x3F] |
		         DesTable[5][v >>> 19    & 0x3F] |
		         DesTable[6][u >>> 23    & 0x3F] |
		         DesTable[7][ROR(v, 27) & 0x3F]);
  }


  static void DesExpandKey56(byte[] key, int pos, int[][] subkeys) {
    byte[] key64 = new byte[8];
    key64[7] = key[pos + 6];
    key64[6] = (byte)(key[pos + 6] << 7 | (key[pos + 5] & 0xFF) >>> 1);
    key64[5] = (byte)(key[pos + 5] << 6 | (key[pos + 4] & 0xFF) >>> 2);
    key64[4] = (byte)(key[pos + 4] << 5 | (key[pos + 3] & 0xFF) >>> 3);
    key64[3] = (byte)(key[pos + 3] << 4 | (key[pos + 2] & 0xFF) >>> 4);
    key64[2] = (byte)(key[pos + 2] << 3 | (key[pos + 1] & 0xFF) >>> 5);
    key64[1] = (byte)(key[pos + 1] << 2 | (key[pos] & 0xFF) >>> 6);
    key64[0] = (byte)(key[pos] << 1);

    int[] uk = new int[2];
    FromBytes(uk, 0, key64, 0, 8);

    RefInt c = new RefInt();
    RefInt d = new RefInt();
    c.v = uk[1]; d.v = uk[0];
    int cc, dd, t;

    // Permuted choice 1

    PERMUTE(d, c, 4, 0x0F0F0F0F);
    PERMUTE(c, d, -16, 0xFFFF0000);
    PERMUTE(c, d, 2, 0x33333333);
    PERMUTE(c, d, 8, 0x00FF00FF);
    PERMUTE(d, c, 1, 0x55555555);
    PERMUTE(c, d, -8, 0xFF00FF00);
    PERMUTE(c, d, -16, 0xFFFF0000);
    c.v = (c.v & 0x00FF00FF) | (ROL(c.v, 16) & 0xFF00FF00);
    c.v = c.v >>> 4 & 0x0FFFFFF0 | d.v & 0xF;
    t = c.v; c.v = d.v >>> 4; d.v = t;

    // Subkey generation loop

    for (int i = 0; i < 16; i++) {
      if (NShifts[i] == 1) {
        c.v = c.v << 1 | (c.v & 0x0FFFFFFF) >>> 27;
        d.v = d.v << 1 | (d.v & 0x0FFFFFFF) >>> 27;
      }
      else {
        c.v = c.v << 2 | (c.v & 0x0FFFFFFF) >>> 26;
        d.v = d.v << 2 | (d.v & 0x0FFFFFFF) >>> 26;
      }

      // Permuted choice 2

      dd = dTable[0][d.v & 0x7F] |
           dTable[1][d.v >>> 7 & 0x7F] |
           dTable[2][d.v >>> 14 & 0x7F] |
           dTable[3][d.v >>> 21 & 0x7F];
      cc = cTable[0][c.v & 0x7F] |
           cTable[1][c.v >>> 7 & 0x7F] |
           cTable[2][c.v >>> 14 & 0x7F] |
           cTable[3][c.v >>> 21 & 0x7F];

      // Reorder six-bit nibbles

      subkeys[0][i] = ROR(dd & 0x3F |
                          dd >>> 4 & 0x3F00 |
                          cc << 16 & 0x3F0000 |
                          cc << 12 & 0x3F000000, 1);
      subkeys[1][i] = ROL(dd >>> 4 & 0xFC |
                          dd >>> 8 & 0xFC00 |
                          cc << 12 & 0xFC0000 |
                          cc << 8 & 0xFC000000, 1);
    }
  }

  static void DesEncryptBlock(int[][] subkeys, byte[] dst, int to, byte[] src, int from) {
    int[] block = new int[2];
    FromBytes(block, 0, src, from, 8);

    RefInt l = new RefInt();
    RefInt r = new RefInt();
    l.v = block[1]; r.v = block[0];

    // Initial permutation

    PERMUTE(r, l, 4, 0x0F0F0F0F);
    PERMUTE(l, r, -16, 0xFFFF0000);
    PERMUTE(l, r, 2, 0x33333333);
    PERMUTE(l, r, 8, 0x00FF00FF);
    PERMUTE(r, l, 1, 0x55555555);

    // Encryption

    ROUND(l, r.v, subkeys, 0);
    ROUND(r, l.v, subkeys, 1);
    ROUND(l, r.v, subkeys, 2);
    ROUND(r, l.v, subkeys, 3);
    ROUND(l, r.v, subkeys, 4);
    ROUND(r, l.v, subkeys, 5);
    ROUND(l, r.v, subkeys, 6);
    ROUND(r, l.v, subkeys, 7);
    ROUND(l, r.v, subkeys, 8);
    ROUND(r, l.v, subkeys, 9);
    ROUND(l, r.v, subkeys, 10);
    ROUND(r, l.v, subkeys, 11);
    ROUND(l, r.v, subkeys, 12);
    ROUND(r, l.v, subkeys, 13);
    ROUND(l, r.v, subkeys, 14);
    ROUND(r, l.v, subkeys, 15);

    // Final permutation

    PERMUTE(l, r, 1, 0x55555555);
    PERMUTE(r, l, 8, 0x00FF00FF);
    PERMUTE(r, l, 2, 0x33333333);
    PERMUTE(r, l, -16, 0xFFFF0000);
    PERMUTE(l, r, 4, 0x0F0F0F0F);

    block[0] = l.v;
    block[1] = r.v;
    ToBytes(dst, to, block, 0, 8);
  }

  static void DesDecryptBlock(int[][] subkeys, byte[] dst, int to, byte[] src, int from) {
    int[] block = new int[2];
    FromBytes(block, 0, src, from, 8);

    RefInt l = new RefInt();
    RefInt r = new RefInt();
    l.v = block[1]; r.v = block[0];

    // Initial permutation

    PERMUTE(r, l, 4, 0x0F0F0F0F);
    PERMUTE(l, r, -16, 0xFFFF0000);
    PERMUTE(l, r, 2, 0x33333333);
    PERMUTE(l, r, 8, 0x00FF00FF);
    PERMUTE(r, l, 1, 0x55555555);

    // Decryption

    ROUND(l, r.v, subkeys, 15);
    ROUND(r, l.v, subkeys, 14);
    ROUND(l, r.v, subkeys, 13);
    ROUND(r, l.v, subkeys, 12);
    ROUND(l, r.v, subkeys, 11);
    ROUND(r, l.v, subkeys, 10);
    ROUND(l, r.v, subkeys, 9);
    ROUND(r, l.v, subkeys, 8);
    ROUND(l, r.v, subkeys, 7);
    ROUND(r, l.v, subkeys, 6);
    ROUND(l, r.v, subkeys, 5);
    ROUND(r, l.v, subkeys, 4);
    ROUND(l, r.v, subkeys, 3);
    ROUND(r, l.v, subkeys, 2);
    ROUND(l, r.v, subkeys, 1);
    ROUND(r, l.v, subkeys, 0);

    // Final permutation

    PERMUTE(l, r, 1, 0x55555555);
    PERMUTE(r, l, 8, 0x00FF00FF);
    PERMUTE(r, l, 2, 0x33333333);
    PERMUTE(r, l, -16, 0xFFFF0000);
    PERMUTE(l, r, 4, 0x0F0F0F0F);

    block[0] = l.v;
    block[1] = r.v;
    ToBytes(dst, to, block, 0, 8);
  }

  void Des3EncryptBlock(int[][] subkeys1, int[][] subkeys2, int[][] subkeys3, byte[] dst, int to, byte[] src, int from) {
    int[] block = new int[2];
    FromBytes(block, 0, src, from, 8);

    RefInt l = new RefInt();
    RefInt r = new RefInt();
    l.v = block[1]; r.v = block[0];

    // Initial permutation

    PERMUTE(r, l, 4, 0x0F0F0F0F);
    PERMUTE(l, r, -16, 0xFFFF0000);
    PERMUTE(l, r, 2, 0x33333333);
    PERMUTE(l, r, 8, 0x00FF00FF);
    PERMUTE(r, l, 1, 0x55555555);

    // Encryption

    ROUND(l, r.v, subkeys1, 0);
    ROUND(r, l.v, subkeys1, 1);
    ROUND(l, r.v, subkeys1, 2);
    ROUND(r, l.v, subkeys1, 3);
    ROUND(l, r.v, subkeys1, 4);
    ROUND(r, l.v, subkeys1, 5);
    ROUND(l, r.v, subkeys1, 6);
    ROUND(r, l.v, subkeys1, 7);
    ROUND(l, r.v, subkeys1, 8);
    ROUND(r, l.v, subkeys1, 9);
    ROUND(l, r.v, subkeys1, 10);
    ROUND(r, l.v, subkeys1, 11);
    ROUND(l, r.v, subkeys1, 12);
    ROUND(r, l.v, subkeys1, 13);
    ROUND(l, r.v, subkeys1, 14);
    ROUND(r, l.v, subkeys1, 15);

    if (cipherScheme == 0) {
      ROUND(r, l.v, subkeys2, 15);
      ROUND(l, r.v, subkeys2, 14);
      ROUND(r, l.v, subkeys2, 13);
      ROUND(l, r.v, subkeys2, 12);
      ROUND(r, l.v, subkeys2, 11);
      ROUND(l, r.v, subkeys2, 10);
      ROUND(r, l.v, subkeys2, 9);
      ROUND(l, r.v, subkeys2, 8);
      ROUND(r, l.v, subkeys2, 7);
      ROUND(l, r.v, subkeys2, 6);
      ROUND(r, l.v, subkeys2, 5);
      ROUND(l, r.v, subkeys2, 4);
      ROUND(r, l.v, subkeys2, 3);
      ROUND(l, r.v, subkeys2, 2);
      ROUND(r, l.v, subkeys2, 1);
      ROUND(l, r.v, subkeys2, 0);
    }
    else {
      ROUND(r, l.v, subkeys2, 0);
      ROUND(l, r.v, subkeys2, 1);
      ROUND(r, l.v, subkeys2, 2);
      ROUND(l, r.v, subkeys2, 3);
      ROUND(r, l.v, subkeys2, 4);
      ROUND(l, r.v, subkeys2, 5);
      ROUND(r, l.v, subkeys2, 6);
      ROUND(l, r.v, subkeys2, 7);
      ROUND(r, l.v, subkeys2, 8);
      ROUND(l, r.v, subkeys2, 9);
      ROUND(r, l.v, subkeys2, 10);
      ROUND(l, r.v, subkeys2, 11);
      ROUND(r, l.v, subkeys2, 12);
      ROUND(l, r.v, subkeys2, 13);
      ROUND(r, l.v, subkeys2, 14);
      ROUND(l, r.v, subkeys2, 15);
    }

    ROUND(l, r.v, subkeys3, 0);
    ROUND(r, l.v, subkeys3, 1);
    ROUND(l, r.v, subkeys3, 2);
    ROUND(r, l.v, subkeys3, 3);
    ROUND(l, r.v, subkeys3, 4);
    ROUND(r, l.v, subkeys3, 5);
    ROUND(l, r.v, subkeys3, 6);
    ROUND(r, l.v, subkeys3, 7);
    ROUND(l, r.v, subkeys3, 8);
    ROUND(r, l.v, subkeys3, 9);
    ROUND(l, r.v, subkeys3, 10);
    ROUND(r, l.v, subkeys3, 11);
    ROUND(l, r.v, subkeys3, 12);
    ROUND(r, l.v, subkeys3, 13);
    ROUND(l, r.v, subkeys3, 14);
    ROUND(r, l.v, subkeys3, 15);

    // Final permutation

    PERMUTE(l, r, 1, 0x55555555);
    PERMUTE(r, l, 8, 0x00FF00FF);
    PERMUTE(r, l, 2, 0x33333333);
    PERMUTE(r, l, -16, 0xFFFF0000);
    PERMUTE(l, r, 4, 0x0F0F0F0F);

    block[0] = l.v;
    block[1] = r.v;
    ToBytes(dst, to, block, 0, 8);
  }

  void Des3DecryptBlock(int[][] subkeys1, int[][] subkeys2, int[][] subkeys3, byte[] dst, int to, byte[] src, int from) {
    int[] block = new int[2];
    FromBytes(block, 0, src, from, 8);

    RefInt l = new RefInt();
    RefInt r = new RefInt();
    l.v = block[1]; r.v = block[0];

    // Initial permutation

    PERMUTE(r, l, 4, 0x0F0F0F0F);
    PERMUTE(l, r, -16, 0xFFFF0000);
    PERMUTE(l, r, 2, 0x33333333);
    PERMUTE(l, r, 8, 0x00FF00FF);
    PERMUTE(r, l, 1, 0x55555555);
    
    // Decryption

    ROUND(l, r.v, subkeys3, 15);
    ROUND(r, l.v, subkeys3, 14);
    ROUND(l, r.v, subkeys3, 13);
    ROUND(r, l.v, subkeys3, 12);
    ROUND(l, r.v, subkeys3, 11);
    ROUND(r, l.v, subkeys3, 10);
    ROUND(l, r.v, subkeys3, 9);
    ROUND(r, l.v, subkeys3, 8);
    ROUND(l, r.v, subkeys3, 7);
    ROUND(r, l.v, subkeys3, 6);
    ROUND(l, r.v, subkeys3, 5);
    ROUND(r, l.v, subkeys3, 4);
    ROUND(l, r.v, subkeys3, 3);
    ROUND(r, l.v, subkeys3, 2);
    ROUND(l, r.v, subkeys3, 1);
    ROUND(r, l.v, subkeys3, 0);

    if (cipherScheme == 0) {
      ROUND(r, l.v, subkeys2, 0);
      ROUND(l, r.v, subkeys2, 1);
      ROUND(r, l.v, subkeys2, 2);
      ROUND(l, r.v, subkeys2, 3);
      ROUND(r, l.v, subkeys2, 4);
      ROUND(l, r.v, subkeys2, 5);
      ROUND(r, l.v, subkeys2, 6);
      ROUND(l, r.v, subkeys2, 7);
      ROUND(r, l.v, subkeys2, 8);
      ROUND(l, r.v, subkeys2, 9);
      ROUND(r, l.v, subkeys2, 10);
      ROUND(l, r.v, subkeys2, 11);
      ROUND(r, l.v, subkeys2, 12);
      ROUND(l, r.v, subkeys2, 13);
      ROUND(r, l.v, subkeys2, 14);
      ROUND(l, r.v, subkeys2, 15);
    }
    else {
      ROUND(r, l.v, subkeys2, 15);
      ROUND(l, r.v, subkeys2, 14);
      ROUND(r, l.v, subkeys2, 13);
      ROUND(l, r.v, subkeys2, 12);
      ROUND(r, l.v, subkeys2, 11);
      ROUND(l, r.v, subkeys2, 10);
      ROUND(r, l.v, subkeys2, 9);
      ROUND(l, r.v, subkeys2, 8);
      ROUND(r, l.v, subkeys2, 7);
      ROUND(l, r.v, subkeys2, 6);
      ROUND(r, l.v, subkeys2, 5);
      ROUND(l, r.v, subkeys2, 4);
      ROUND(r, l.v, subkeys2, 3);
      ROUND(l, r.v, subkeys2, 2);
      ROUND(r, l.v, subkeys2, 1);
      ROUND(l, r.v, subkeys2, 0);
    }

    ROUND(l, r.v, subkeys1, 15);
    ROUND(r, l.v, subkeys1, 14);
    ROUND(l, r.v, subkeys1, 13);
    ROUND(r, l.v, subkeys1, 12);
    ROUND(l, r.v, subkeys1, 11);
    ROUND(r, l.v, subkeys1, 10);
    ROUND(l, r.v, subkeys1, 9);
    ROUND(r, l.v, subkeys1, 8);
    ROUND(l, r.v, subkeys1, 7);
    ROUND(r, l.v, subkeys1, 6);
    ROUND(l, r.v, subkeys1, 5);
    ROUND(r, l.v, subkeys1, 4);
    ROUND(l, r.v, subkeys1, 3);
    ROUND(r, l.v, subkeys1, 2);
    ROUND(l, r.v, subkeys1, 1);
    ROUND(r, l.v, subkeys1, 0);

    // Final permutation

    PERMUTE(l, r, 1, 0x55555555);
    PERMUTE(r, l, 8, 0x00FF00FF);
    PERMUTE(r, l, 2, 0x33333333);
    PERMUTE(r, l, -16, 0xFFFF0000);
    PERMUTE(l, r, 4, 0x0F0F0F0F);

    block[0] = l.v;
    block[1] = r.v;
    ToBytes(dst, to, block, 0, 8);
  }
}

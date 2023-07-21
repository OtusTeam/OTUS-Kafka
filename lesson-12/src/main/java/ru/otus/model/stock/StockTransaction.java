/*
 * Copyright 2016 Bill Bejeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.otus.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Date;


@Builder(toBuilder = true)
@Value
@AllArgsConstructor
public class StockTransaction {

    String symbol;
    String sector;
    String industry;
    int shares;
    double sharePrice;
    String customerId;
    Date transactionTimestamp;
    boolean purchase;


    public static StockTransaction reduce(StockTransaction transactionOne, StockTransaction transactionTwo) {
        var transactionBuilder = transactionOne.toBuilder();
        transactionBuilder.shares(transactionOne.getShares() + transactionTwo.getShares());
        return transactionBuilder.build();
    }
}

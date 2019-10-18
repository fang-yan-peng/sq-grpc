package com.sq.rpc.cluster;

import java.util.List;

import com.sq.common.URL;
import com.sq.common.extension.SPI;

@SPI
public interface RuleConverter {

    List<URL> convert(URL subscribeUrl, Object source);

}

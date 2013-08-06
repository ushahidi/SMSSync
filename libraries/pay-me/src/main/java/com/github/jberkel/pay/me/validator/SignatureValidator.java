package com.github.jberkel.pay.me.validator;

/**
 * Validates signatures returned by the billing services.
 */
public interface SignatureValidator {
    /**
     * Validates that the specified signature matches the computed signature on
     * the specified signed data. Returns true if the data is correctly signed.
     *
     * @param signedData signed data
     * @param signature signature
     * @return true if the data and signature match, false otherwise.
     */
    boolean validate(String signedData, String signature);
}
